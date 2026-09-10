package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.TripEntity
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary

import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import com.example.data.local.CollaboratorRole

@Composable
fun TripCard(
    trip: TripEntity,
    progressPercent: Int,
    currencySymbol: String,
    currentUserRole: CollaboratorRole = CollaboratorRole.OWNER,
    collaboratorCount: Int = 1,
    onViewTrip: () -> Unit,
    onEditTrip: () -> Unit,
    onDeleteTrip: () -> Unit,
    onCollaboratorsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trip_card_${trip.id}")
            .clickable(onClick = onViewTrip),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Cover Image Banner with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (trip.coverImageUrl == "img_hero_goa" || trip.coverImageUrl.isBlank()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_goa),
                        contentDescription = trip.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                } else {
                    AsyncImage(
                        model = trip.coverImageUrl,
                        contentDescription = trip.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        error = painterResource(id = R.drawable.img_hero_goa),
                        fallback = painterResource(id = R.drawable.img_hero_goa)
                    )
                }

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Top Badge: Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (trip.status.uppercase()) {
                            "ONGOING" -> EmeraldAccent.copy(alpha = 0.9f)
                            "COMPLETED" -> Color(0xFF64748B).copy(alpha = 0.9f)
                            else -> SkyBluePrimary.copy(alpha = 0.9f)
                        }
                    ) {
                        Text(
                            text = trip.status.uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Budget Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Budget: $currencySymbol${String.format("%,.0f", trip.budget)}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Bottom Overlay: Title & Destination
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SkyBlueLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = trip.destination,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Card Body Details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Date & Travelers Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${trip.startDate} – ${trip.endDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = EmeraldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${trip.travelers} Travelers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Collaboration Status & Members
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Role Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (currentUserRole) {
                            CollaboratorRole.OWNER -> EmeraldAccent.copy(alpha = 0.12f)
                            CollaboratorRole.EDITOR -> SkyBluePrimary.copy(alpha = 0.12f)
                            CollaboratorRole.VIEWER -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = when (currentUserRole) {
                                CollaboratorRole.OWNER -> "👑 You are Owner"
                                CollaboratorRole.EDITOR -> "✏️ Shared (Editor)"
                                CollaboratorRole.VIEWER -> "👀 Shared (Viewer)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (currentUserRole) {
                                CollaboratorRole.OWNER -> EmeraldAccent
                                CollaboratorRole.EDITOR -> SkyBluePrimary
                                CollaboratorRole.VIEWER -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Collaborators Pill & Invite Shortcut
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clickable(onClick = onCollaboratorsClick)
                            .testTag("trip_card_collab_button_${trip.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Collaborators",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$collaboratorCount member${if (collaboratorCount > 1) "s" else ""}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (currentUserRole != CollaboratorRole.VIEWER) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Invite",
                                    tint = EmeraldAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Planning Progress
                TripProgressBar(
                    progressPercent = progressPercent,
                    label = "Planning Progress",
                    height = 8.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewTrip,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("view_trip_button_${trip.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Trip", fontWeight = FontWeight.SemiBold)
                    }

                    if (currentUserRole != CollaboratorRole.VIEWER) {
                        OutlinedButton(
                            onClick = onEditTrip,
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("edit_trip_button_${trip.id}"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Trip",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (currentUserRole == CollaboratorRole.OWNER) {
                        IconButton(
                            onClick = onDeleteTrip,
                            modifier = Modifier.testTag("delete_trip_button_${trip.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Trip",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
