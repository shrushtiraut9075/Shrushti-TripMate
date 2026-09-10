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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ActivityEntity
import com.example.data.local.TripEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary

@Composable
fun ItineraryTab(
    trip: TripEntity,
    activities: List<ActivityEntity>,
    onAddActivityClick: (defaultDate: String) -> Unit,
    onEditActivityClick: (ActivityEntity) -> Unit,
    onDeleteActivityClick: (Long) -> Unit,
    onReorderActivities: (List<ActivityEntity>) -> Unit,
    modifier: Modifier = Modifier
) {
    // Unique dates from activities, or default to trip start date
    val dates = activities.map { it.date }.distinct().ifEmpty { listOf(trip.startDate) }
    var selectedDate by remember(dates) { mutableStateOf(dates.firstOrNull() ?: trip.startDate) }

    val dayActivities = activities.filter { it.date == selectedDate }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("itinerary_tab")
    ) {
        // Day selector chips
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Days & Dates",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { onAddActivityClick(selectedDate) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("itinerary_add_activity_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Activity", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(dates) { index, date ->
                        val isSelected = date == selectedDate
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDate = date },
                            label = {
                                Text(
                                    text = "Day ${index + 1} • $date",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Timeline content
        if (dayActivities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No activities on $selectedDate",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'Add Activity' to schedule transport, hotel check-ins, meals, sightseeing, and adventures.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(onClick = { onAddActivityClick(selectedDate) }) {
                            Text("+ Add First Activity")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(dayActivities) { index, activity ->
                    TimelineActivityNode(
                        activity = activity,
                        isFirst = index == 0,
                        isLast = index == dayActivities.size - 1,
                        canMoveUp = index > 0,
                        canMoveDown = index < dayActivities.size - 1,
                        onEdit = { onEditActivityClick(activity) },
                        onDelete = { onDeleteActivityClick(activity.id) },
                        onMoveUp = {
                            val mutableList = dayActivities.toMutableList()
                            val temp = mutableList[index]
                            mutableList[index] = mutableList[index - 1]
                            mutableList[index - 1] = temp
                            onReorderActivities(mutableList)
                        },
                        onMoveDown = {
                            val mutableList = dayActivities.toMutableList()
                            val temp = mutableList[index]
                            mutableList[index] = mutableList[index + 1]
                            mutableList[index + 1] = temp
                            onReorderActivities(mutableList)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineActivityNode(
    activity: ActivityEntity,
    isFirst: Boolean,
    isLast: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val (categoryColor, categoryIcon) = getCategoryStyling(activity.category)
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("itinerary_item_${activity.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Timeline Spine
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Node Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = activity.category,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Connecting line if not last item
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Activity Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onEdit),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Time & Category Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${activity.startTime} – ${activity.endTime}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SkyBluePrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = activity.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    // More Menu for actions
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Actions",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Activity") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            if (canMoveUp) {
                                DropdownMenuItem(
                                    text = { Text("Move Up") },
                                    leadingIcon = { Icon(Icons.Default.ArrowUpward, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        onMoveUp()
                                    }
                                )
                            }
                            if (canMoveDown) {
                                DropdownMenuItem(
                                    text = { Text("Move Down") },
                                    leadingIcon = { Icon(Icons.Default.ArrowDownward, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        onMoveDown()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Delete Activity", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activity.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (activity.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

fun getCategoryStyling(category: String): Pair<Color, ImageVector> {
    return when (category.lowercase()) {
        "transport" -> Pair(SkyBluePrimary, Icons.Default.DirectionsBus)
        "hotel" -> Pair(Color(0xFF8B5CF6), Icons.Default.Hotel)
        "food" -> Pair(AmberAccent, Icons.Default.Fastfood)
        "sightseeing" -> Pair(EmeraldAccent, Icons.Default.Explore)
        "shopping" -> Pair(Color(0xFFEC4899), Icons.Default.ShoppingBag)
        "adventure" -> Pair(Color(0xFFEF4444), Icons.Default.LocalActivity)
        else -> Pair(SkyBlueDark, Icons.Default.Place)
    }
}
