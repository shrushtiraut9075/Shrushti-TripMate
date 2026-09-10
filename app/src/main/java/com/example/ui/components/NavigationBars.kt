package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ConnectingAirports
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBluePrimary
import androidx.compose.material.icons.filled.AccountCircle
import com.example.data.auth.AppUser

enum class AppDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    MY_TRIPS("My Trips", Icons.Default.Luggage),
    WORKSPACE("Workspace", Icons.Default.Explore),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTopBar(
    title: String = "TripMate",
    subtitle: String? = "Smart Travel Planner",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    hasReminders: Boolean = true,
    currentUser: AppUser? = null,
    onUserClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!showBackButton) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "TripMate Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_bar_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.testTag("top_bar_notifications_button")
            ) {
                BadgedBox(badge = {
                    if (hasReminders) {
                        Badge(containerColor = EmeraldAccent)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications & Reminders"
                    )
                }
            }

            // User Profile / Auth Button
            IconButton(
                onClick = onUserClick,
                modifier = Modifier.testTag("top_bar_auth_button")
            ) {
                if (currentUser != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SkyBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.displayName.take(1).uppercase(),
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account Sign In",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("top_bar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun TripBottomNavigationBar(
    currentDestination: AppDestination,
    hasActiveTrip: Boolean,
    onDestinationSelected: (AppDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        AppDestination.values().forEach { destination ->
            val isEnabled = if (destination == AppDestination.WORKSPACE) hasActiveTrip else true
            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = {
                    if (isEnabled) {
                        onDestinationSelected(destination)
                    }
                },
                enabled = isEnabled,
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (currentDestination == destination) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("bottom_nav_${destination.name.lowercase()}")
            )
        }
    }
}
