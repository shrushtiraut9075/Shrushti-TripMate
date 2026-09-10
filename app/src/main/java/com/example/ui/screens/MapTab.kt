package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ActivityEntity
import com.example.data.local.TripEntity
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import kotlin.math.max
import kotlin.math.min

data class MapLocationMarker(
    val id: Long,
    val title: String,
    val locationName: String,
    val category: String,
    val date: String,
    val time: String,
    val description: String,
    val lat: Double,
    val lng: Double
)

@Composable
fun MapTab(
    trip: TripEntity,
    activities: List<ActivityEntity>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showRoute by remember { mutableStateOf(true) }
    var selectedMarker by remember { mutableStateOf<MapLocationMarker?>(null) }

    // Interactive zoom and pan state
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Convert activities into MapLocationMarkers
    val allMarkers = remember(activities, trip) {
        if (activities.isNotEmpty()) {
            activities.map { act ->
                MapLocationMarker(
                    id = act.id,
                    title = act.name,
                    locationName = act.location,
                    category = act.category,
                    date = act.date,
                    time = "${act.startTime} – ${act.endTime}",
                    description = act.description,
                    lat = if (act.latitude != 0.0) act.latitude else 15.4989,
                    lng = if (act.longitude != 0.0) act.longitude else 73.8278
                )
            }
        } else {
            listOf(
                MapLocationMarker(
                    id = 1L,
                    title = trip.name,
                    locationName = trip.destination,
                    category = "Sightseeing",
                    date = trip.startDate,
                    time = "Full Day",
                    description = trip.description,
                    lat = 15.4989,
                    lng = 73.8278
                )
            )
        }
    }

    val categories = listOf("All", "Sightseeing", "Hotel", "Food", "Transport", "Adventure", "Shopping")

    val filteredMarkers = remember(allMarkers, searchQuery, selectedCategory) {
        allMarkers.filter { marker ->
            val matchesCategory = selectedCategory == "All" || marker.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    marker.title.contains(searchQuery, ignoreCase = true) ||
                    marker.locationName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // Determine geographic bounds for relative canvas positioning
    val minLat = (allMarkers.minOfOrNull { it.lat } ?: 15.3).let { it - 0.05 }
    val maxLat = (allMarkers.maxOfOrNull { it.lat } ?: 15.6).let { it + 0.05 }
    val minLng = (allMarkers.minOfOrNull { it.lng } ?: 73.7).let { it - 0.05 }
    val maxLng = (allMarkers.maxOfOrNull { it.lng } ?: 74.1).let { it + 0.05 }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_tab")
    ) {
        // Map Canvas with interactive gesture detection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 3.5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw Water background (Ocean / Bay)
                drawRect(color = Color(0xFFE0F2FE))

                // Draw Coastal Landmass / Terrain
                val terrainPath = Path().apply {
                    moveTo(w * 0.25f, 0f)
                    cubicTo(
                        w * 0.35f, h * 0.2f,
                        w * 0.20f, h * 0.45f,
                        w * 0.30f, h * 0.7f
                    )
                    cubicTo(
                        w * 0.40f, h * 0.85f,
                        w * 0.25f, h * 0.95f,
                        w * 0.35f, h
                    )
                    lineTo(w, h)
                    lineTo(w, 0f)
                    close()
                }
                drawPath(
                    path = terrainPath,
                    color = Color(0xFFF1F5F9)
                )

                // Draw Coastal Beach Sand Fringe
                val sandPath = Path().apply {
                    moveTo(w * 0.25f, 0f)
                    cubicTo(w * 0.35f, h * 0.2f, w * 0.20f, h * 0.45f, w * 0.30f, h * 0.7f)
                    cubicTo(w * 0.40f, h * 0.85f, w * 0.25f, h * 0.95f, w * 0.35f, h)
                }
                drawPath(
                    path = sandPath,
                    color = Color(0xFFFEF3C7),
                    style = Stroke(width = 12f)
                )

                // Draw River Backwaters (Mandovi / Zuari)
                val riverPath = Path().apply {
                    moveTo(w * 0.28f, h * 0.48f)
                    quadraticBezierTo(w * 0.5f, h * 0.45f, w * 0.8f, h * 0.52f)
                    quadraticBezierTo(w * 0.9f, h * 0.55f, w, h * 0.50f)
                }
                drawPath(
                    path = riverPath,
                    color = Color(0xFFBAE6FD),
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )

                // Draw Grid lines
                val gridSpacing = 80f * scale
                var x = (offsetX % gridSpacing)
                while (x < w) {
                    drawLine(
                        color = Color(0xFFCBD5E1).copy(alpha = 0.3f),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }
                var y = (offsetY % gridSpacing)
                while (y < h) {
                    drawLine(
                        color = Color(0xFFCBD5E1).copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                // Map route points calculation
                val points = allMarkers.map { marker ->
                    val normX = ((marker.lng - minLng) / (maxLng - minLng)).coerceIn(0.0, 1.0).toFloat()
                    // Invert lat for canvas Y (higher lat is top)
                    val normY = (1.0f - ((marker.lat - minLat) / (maxLat - minLat)).coerceIn(0.0, 1.0).toFloat())

                    val baseCanvasX = (w * 0.28f) + normX * (w * 0.62f)
                    val baseCanvasY = (h * 0.12f) + normY * (h * 0.76f)

                    val finalX = (baseCanvasX - w / 2) * scale + w / 2 + offsetX
                    val finalY = (baseCanvasY - h / 2) * scale + h / 2 + offsetY
                    Pair(marker, Offset(finalX, finalY))
                }

                // Draw Route Polyline if enabled
                if (showRoute && points.size >= 2) {
                    val routePath = Path()
                    points.forEachIndexed { index, (_, offset) ->
                        if (index == 0) routePath.moveTo(offset.x, offset.y)
                        else routePath.lineTo(offset.x, offset.y)
                    }

                    // Outer glowing trail
                    drawPath(
                        path = routePath,
                        color = SkyBluePrimary.copy(alpha = 0.25f),
                        style = Stroke(
                            width = 10f * scale.coerceIn(0.8f, 2f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Dashed line
                    drawPath(
                        path = routePath,
                        color = SkyBluePrimary,
                        style = Stroke(
                            width = 3.5f * scale.coerceIn(0.8f, 2f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Draw Markers
                points.forEachIndexed { index, (marker, offset) ->
                    val isSelected = selectedMarker?.id == marker.id
                    val isVisible = filteredMarkers.any { it.id == marker.id }

                    if (isVisible) {
                        val (catColor, _) = getCategoryStyling(marker.category)
                        val radius = if (isSelected) 18f * scale.coerceIn(0.8f, 1.5f) else 13f * scale.coerceIn(0.8f, 1.5f)

                        // Outer pulse ring for selected marker
                        if (isSelected) {
                            drawCircle(
                                color = catColor.copy(alpha = 0.3f),
                                radius = radius * 2.0f,
                                center = offset
                            )
                        }

                        // White border
                        drawCircle(
                            color = Color.White,
                            radius = radius + 4f,
                            center = offset
                        )

                        // Main pin circle
                        drawCircle(
                            color = catColor,
                            radius = radius,
                            center = offset
                        )

                        // Inner dot
                        drawCircle(
                            color = Color.White,
                            radius = radius * 0.4f,
                            center = offset
                        )
                    }
                }
            }

            // Click overlay overlaying canvas for markers
            val points = allMarkers.map { marker ->
                val normX = ((marker.lng - minLng) / (maxLng - minLng)).coerceIn(0.0, 1.0).toFloat()
                val normY = (1.0f - ((marker.lat - minLat) / (maxLat - minLat)).coerceIn(0.0, 1.0).toFloat())
                val baseCanvasX = 0.28f + normX * 0.62f
                val baseCanvasY = 0.12f + normY * 0.76f
                Pair(marker, Pair(baseCanvasX, baseCanvasY))
            }
        }

        // Top Overlay: Search Bar & Filter Chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("map_search_bar"),
                        placeholder = { Text("Search location or activity...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSel = selectedCategory == cat
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Demo / Offline Mode Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Interactive Smart Map • ${filteredMarkers.size} Spots Plotted",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Floating Map Controls (Zoom +, Zoom -, Reset, Route Toggle)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FloatingActionButton(
                onClick = { scale = (scale * 1.3f).coerceAtMost(3.5f) },
                shape = CircleShape,
                modifier = Modifier.size(44.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            FloatingActionButton(
                onClick = { scale = (scale / 1.3f).coerceAtLeast(0.6f) },
                shape = CircleShape,
                modifier = Modifier.size(44.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            FloatingActionButton(
                onClick = {
                    scale = 1.0f
                    offsetX = 0f
                    offsetY = 0f
                },
                shape = CircleShape,
                modifier = Modifier.size(44.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = "Reset View")
            }

            FloatingActionButton(
                onClick = { showRoute = !showRoute },
                shape = CircleShape,
                modifier = Modifier.size(44.dp),
                containerColor = if (showRoute) SkyBluePrimary else MaterialTheme.colorScheme.surface,
                contentColor = if (showRoute) Color.White else MaterialTheme.colorScheme.onSurface,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.Route, contentDescription = "Toggle Route")
            }
        }

        // Bottom Map Pins Selector Row & Marker Info Sheet
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Horizontal carousel of pin chips for quick selection
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMarkers) { marker ->
                    val isSelected = selectedMarker?.id == marker.id
                    val (color, _) = getCategoryStyling(marker.category)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp,
                        modifier = Modifier.clickable { selectedMarker = marker }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = marker.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Expanded Marker Info Card when a pin is selected
            AnimatedVisibility(
                visible = selectedMarker != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                selectedMarker?.let { marker ->
                    val (catColor, catIcon) = getCategoryStyling(marker.category)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(catColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = catIcon,
                                            contentDescription = null,
                                            tint = catColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = marker.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${marker.date} • ${marker.time}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                IconButton(onClick = { selectedMarker = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = marker.locationName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (marker.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = marker.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
