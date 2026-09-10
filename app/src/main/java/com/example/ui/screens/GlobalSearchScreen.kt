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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ActivityEntity
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TripEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.viewmodel.WorkspaceTab

data class SearchResult(
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val iconColor: Color,
    val tripId: Long,
    val targetTab: WorkspaceTab
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    trips: List<TripEntity>,
    activities: List<ActivityEntity>,
    checklist: List<ChecklistItemEntity>,
    notes: List<NoteEntity>,
    onNavigateToTripTab: (tripId: Long, tab: WorkspaceTab) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val results = remember(searchQuery, trips, activities, checklist, notes) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val list = mutableListOf<SearchResult>()
            val q = searchQuery.trim()

            // Trips
            trips.filter { it.name.contains(q, ignoreCase = true) || it.destination.contains(q, ignoreCase = true) }
                .forEach {
                    list.add(
                        SearchResult(
                            title = it.name,
                            subtitle = "${it.destination} • ${it.startDate} – ${it.endDate}",
                            category = "Trip",
                            icon = Icons.Default.FlightTakeoff,
                            iconColor = SkyBluePrimary,
                            tripId = it.id,
                            targetTab = WorkspaceTab.ITINERARY
                        )
                    )
                }

            // Activities & Locations
            activities.filter {
                it.name.contains(q, ignoreCase = true) ||
                        it.location.contains(q, ignoreCase = true) ||
                        it.description.contains(q, ignoreCase = true)
            }.forEach {
                list.add(
                    SearchResult(
                        title = it.name,
                        subtitle = "${it.location} • ${it.date} (${it.startTime})",
                        category = "Activity (${it.category})",
                        icon = Icons.Default.EventNote,
                        iconColor = EmeraldAccent,
                        tripId = it.tripId,
                        targetTab = WorkspaceTab.ITINERARY
                    )
                )
            }

            // Checklist
            checklist.filter { it.title.contains(q, ignoreCase = true) || it.category.contains(q, ignoreCase = true) }
                .forEach {
                    list.add(
                        SearchResult(
                            title = it.title,
                            subtitle = "Checklist: ${it.category} • ${if (it.isCompleted) "Completed" else "Pending"}",
                            category = "Checklist",
                            icon = Icons.Default.AssignmentTurnedIn,
                            iconColor = if (it.isCompleted) EmeraldAccent else AmberAccent,
                            tripId = it.tripId,
                            targetTab = WorkspaceTab.CHECKLIST
                        )
                    )
                }

            // Notes
            notes.filter {
                it.title.contains(q, ignoreCase = true) ||
                        it.content.contains(q, ignoreCase = true) ||
                        it.category.contains(q, ignoreCase = true)
            }.forEach {
                list.add(
                    SearchResult(
                        title = it.title,
                        subtitle = "Note: ${it.category}",
                        category = "Note",
                        icon = Icons.Default.Description,
                        iconColor = Color(0xFF8B5CF6),
                        tripId = it.tripId,
                        targetTab = WorkspaceTab.NOTES
                    )
                )
            }

            list
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("global_search_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Global Search", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search trips, activities, locations, tasks, notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input")
                )
            }

            if (searchQuery.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Search across your entire travel plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Find destinations, hotels, flights, packing items, or pinned notes instantly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (results.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found for \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${results.size} Results Found",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    items(results) { result ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onNavigateToTripTab(result.tripId, result.targetTab)
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(result.iconColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = result.icon,
                                        contentDescription = null,
                                        tint = result.iconColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = result.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = result.iconColor.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = result.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = result.iconColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = result.subtitle,
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
