package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ActivityEntity
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TripEntity
import com.example.ui.components.TripCard
import com.example.ui.components.TripProgressBar
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBlueDark
import com.example.data.auth.AppUser
import com.example.data.local.CollaboratorRole
import com.example.data.local.TripCollaboratorEntity
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary

@Composable
fun DashboardScreen(
    trips: List<TripEntity>,
    activities: List<ActivityEntity>,
    checklist: List<ChecklistItemEntity>,
    notes: List<NoteEntity>,
    expenses: List<ExpenseEntity>,
    currencySymbol: String,
    currentUser: AppUser? = null,
    allCollaborators: List<TripCollaboratorEntity> = emptyList(),
    onCreateTripClick: () -> Unit,
    onViewTripClick: (Long) -> Unit,
    onEditTripClick: (TripEntity) -> Unit,
    onDeleteTripClick: (Long) -> Unit,
    onCollaboratorsClick: (TripEntity) -> Unit = {},
    getTripProgress: (TripEntity) -> Int,
    modifier: Modifier = Modifier
) {
    val upcomingTrips = trips.filter { it.status.equals("UPCOMING", ignoreCase = true) }
    val ongoingTrips = trips.filter { it.status.equals("ONGOING", ignoreCase = true) }
    val completedTrips = trips.filter { it.status.equals("COMPLETED", ignoreCase = true) }

    val distinctDestinations = trips.map { it.destination }.distinct().size
    val totalPlannedActivities = activities.size
    val completedChecklist = checklist.count { it.isCompleted }
    val checklistPercent = if (checklist.isNotEmpty()) ((completedChecklist.toDouble() / checklist.size) * 100).toInt() else 0
    val totalSpent = expenses.sumOf { it.amount }
    val totalBudget = trips.sumOf { it.budget }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcome & Hero Tagline Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SkyBluePrimary,
                                    SkyBlueDark,
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Ready to explore?",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = EmeraldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Plan your journey.\nOrganize your adventure.",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Manage itineraries, interactive maps, packing checklists, notes, and budgets in one place.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // + Create New Trip prominent button
                        Button(
                            onClick = onCreateTripClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldAccent,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("dashboard_create_trip_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+ Create New Trip",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Summary Grid
        item {
            Column {
                Text(
                    text = "Travel Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = "Destinations",
                        value = distinctDestinations.toString(),
                        icon = Icons.Default.Place,
                        iconColor = SkyBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Planned Activities",
                        value = totalPlannedActivities.toString(),
                        icon = Icons.Default.EventNote,
                        iconColor = EmeraldAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = "Checklist Done",
                        value = "$checklistPercent%",
                        subtitle = "$completedChecklist / ${checklist.size} tasks",
                        icon = Icons.Default.CheckCircle,
                        iconColor = EmeraldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Budget Spent",
                        value = "$currencySymbol${String.format("%,.0f", totalSpent)}",
                        subtitle = "of $currencySymbol${String.format("%,.0f", totalBudget)}",
                        icon = Icons.Default.Payments,
                        iconColor = AmberAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Trip Categories Counter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryCountPill(
                    label = "Upcoming",
                    count = upcomingTrips.size,
                    color = SkyBluePrimary,
                    modifier = Modifier.weight(1f)
                )
                CategoryCountPill(
                    label = "Ongoing",
                    count = ongoingTrips.size,
                    color = EmeraldAccent,
                    modifier = Modifier.weight(1f)
                )
                CategoryCountPill(
                    label = "Completed",
                    count = completedTrips.size,
                    color = Color(0xFF64748B),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Featured Trips Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current & Upcoming Trips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${trips.size} Trips",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (trips.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Luggage,
                            contentDescription = null,
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No trips planned yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap '+ Create New Trip' above to start organizing your next great adventure.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(trips) { trip ->
                val userEmail = currentUser?.email?.lowercase() ?: ""
                val userId = currentUser?.uid ?: ""
                val isOwner = trip.creatorEmail.equals(userEmail, ignoreCase = true) || trip.creatorId == userId
                val userRole = if (isOwner) {
                    CollaboratorRole.OWNER
                } else {
                    val collab = allCollaborators.firstOrNull {
                        it.tripId == trip.id && it.email.equals(userEmail, ignoreCase = true)
                    }
                    when (collab?.role?.uppercase()) {
                        "OWNER" -> CollaboratorRole.OWNER
                        "EDITOR" -> CollaboratorRole.EDITOR
                        else -> CollaboratorRole.VIEWER
                    }
                }
                val collabCount = allCollaborators.count { it.tripId == trip.id }.coerceAtLeast(1)

                TripCard(
                    trip = trip,
                    progressPercent = getTripProgress(trip),
                    currencySymbol = currencySymbol,
                    currentUserRole = userRole,
                    collaboratorCount = collabCount,
                    onViewTrip = { onViewTripClick(trip.id) },
                    onEditTrip = { onEditTripClick(trip) },
                    onDeleteTrip = { onDeleteTripClick(trip.id) },
                    onCollaboratorsClick = { onCollaboratorsClick(trip) }
                )
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCountPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
