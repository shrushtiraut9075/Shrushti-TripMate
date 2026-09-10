package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ActivityEntity
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.TripEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBluePrimary

data class ReminderItem(
    val title: String,
    val subtitle: String,
    val tag: String,
    val icon: ImageVector,
    val iconColor: Color,
    val timeLabel: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    trips: List<TripEntity>,
    activities: List<ActivityEntity>,
    checklist: List<ChecklistItemEntity>,
    onDismiss: () -> Unit
) {
    // Generate dynamic reminders based on real data
    val reminders = mutableListOf<ReminderItem>()

    // 1. Upcoming trips
    trips.firstOrNull { it.status.equals("UPCOMING", ignoreCase = true) }?.let { trip ->
        reminders.add(
            ReminderItem(
                title = "Upcoming Trip: ${trip.name}",
                subtitle = "Starting on ${trip.startDate} to ${trip.destination}",
                tag = "Trip Countdown",
                icon = Icons.Default.FlightTakeoff,
                iconColor = SkyBluePrimary,
                timeLabel = trip.startDate
            )
        )
    }

    // 2. Transport departures & Hotel Check-in
    activities.filter { it.category == "Transport" }.take(1).forEach { act ->
        reminders.add(
            ReminderItem(
                title = "Transport: ${act.name}",
                subtitle = "Departing at ${act.startTime} from ${act.location}",
                tag = "Departure",
                icon = Icons.Default.Alarm,
                iconColor = AmberAccent,
                timeLabel = "${act.date} • ${act.startTime}"
            )
        )
    }

    activities.filter { it.category == "Hotel" }.take(1).forEach { act ->
        reminders.add(
            ReminderItem(
                title = "Hotel Check-in: ${act.name}",
                subtitle = "Check-in at ${act.startTime} at ${act.location}",
                tag = "Accommodation",
                icon = Icons.Default.Hotel,
                iconColor = SkyBluePrimary,
                timeLabel = "${act.date} • ${act.startTime}"
            )
        )
    }

    // 3. Pending checklist items
    val pendingTasks = checklist.filter { !it.isCompleted }
    if (pendingTasks.isNotEmpty()) {
        reminders.add(
            ReminderItem(
                title = "${pendingTasks.size} Pending Travel Tasks",
                subtitle = "Don't forget: ${pendingTasks.take(2).joinToString(", ") { it.title }}",
                tag = "Action Needed",
                icon = Icons.Default.AssignmentTurnedIn,
                iconColor = EmeraldAccent,
                timeLabel = "Before Departure"
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Travel Reminders & Alerts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You're all caught up! No active reminders.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reminders) { reminder ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(reminder.iconColor.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = reminder.icon,
                                        contentDescription = null,
                                        tint = reminder.iconColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = reminder.tag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = reminder.iconColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = reminder.timeLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = reminder.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = reminder.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
