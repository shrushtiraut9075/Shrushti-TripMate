package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.auth.AppUser
import com.example.data.local.CollaboratorRole
import com.example.data.local.TripCollaboratorEntity
import com.example.data.local.TripEntity
import com.example.ui.components.TripCard
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.viewmodel.TripFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    trips: List<TripEntity>,
    currentFilter: TripFilter,
    searchQuery: String,
    currencySymbol: String,
    currentUser: AppUser? = null,
    allCollaborators: List<TripCollaboratorEntity> = emptyList(),
    onFilterChange: (TripFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onCreateTripClick: () -> Unit,
    onViewTripClick: (Long) -> Unit,
    onEditTripClick: (TripEntity) -> Unit,
    onDeleteTripClick: (Long) -> Unit,
    onCollaboratorsClick: (TripEntity) -> Unit = {},
    getTripProgress: (TripEntity) -> Int,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("trips_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTripClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("trips_fab_create")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New Trip")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar & Filter Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trips_search_bar"),
                    placeholder = { Text("Search by trip name or destination...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filters: All, Created By Me, Shared With Me, Upcoming, Ongoing, Completed
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TripFilter.values()) { filter ->
                        val isSelected = currentFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(filter) },
                            label = {
                                Text(
                                    text = filter.label,
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

            // Trips List
            if (trips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
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
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No matching trips found" else "No trips in this category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try adjusting your search query or create a new trip using the + button below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
    }
}
