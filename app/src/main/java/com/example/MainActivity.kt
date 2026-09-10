package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.auth.AppUser
import com.example.data.local.CollaboratorRole
import com.example.data.local.TripEntity
import com.example.ui.components.AppDestination
import com.example.ui.components.AuthDialog
import com.example.ui.components.CollaboratorsDialog
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.CreateEditTripDialog
import com.example.ui.components.NotificationsSheet
import com.example.ui.components.TripBottomNavigationBar
import com.example.ui.components.TripTopBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TripWorkspaceScreen
import com.example.ui.screens.TripsScreen
import com.example.ui.theme.TripMateTheme
import com.example.ui.viewmodel.TripViewModel
import com.example.ui.viewmodel.WorkspaceTab
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripMateTheme {
                TripMateApp()
            }
        }
    }
}

@Composable
fun TripMateApp(
    viewModel: TripViewModel = viewModel()
) {
    val allTrips by viewModel.allTrips.collectAsState()
    val filteredTrips by viewModel.filteredTrips.collectAsState()
    val tripFilter by viewModel.tripFilter.collectAsState()
    val tripSearchQuery by viewModel.tripSearchQuery.collectAsState()

    val selectedTripId by viewModel.selectedTripId.collectAsState()
    val currentTrip by viewModel.currentTrip.collectAsState()
    val currentActivities by viewModel.currentActivities.collectAsState()
    val currentChecklist by viewModel.currentChecklist.collectAsState()
    val currentNotes by viewModel.currentNotes.collectAsState()
    val currentExpenses by viewModel.currentExpenses.collectAsState()
    val tripProgress by viewModel.tripProgress.collectAsState()
    val activeWorkspaceTab by viewModel.activeWorkspaceTab.collectAsState()
    val currencySymbol by viewModel.currency.collectAsState()

    val allActivities by viewModel.allActivities.collectAsState()
    val allChecklistItems by viewModel.allChecklistItems.collectAsState()
    val allNotes by viewModel.allNotes.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()

    // Auth & Collaboration State
    val currentUser by viewModel.currentUser.collectAsState()
    val isFirebaseAvailable by viewModel.isFirebaseAvailable.collectAsState()
    val isAuthDialogVisible by viewModel.isAuthDialogVisible.collectAsState()
    val isCollaboratorsDialogVisible by viewModel.isCollaboratorsDialogVisible.collectAsState()
    val allCollaborators by viewModel.allCollaborators.collectAsState()
    val currentTripCollaborators by viewModel.currentTripCollaborators.collectAsState()
    val currentUserTripRole by viewModel.currentUserTripRole.collectAsState()
    val isLiveSyncConnected by viewModel.isLiveSyncConnected.collectAsState()
    var tripForCollaborators by remember { mutableStateOf<TripEntity?>(null) }

    // Navigation State
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }
    var isSearchActive by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    // Dialog States
    var showCreateTripDialog by remember { mutableStateOf(false) }
    var tripToEdit by remember { mutableStateOf<TripEntity?>(null) }
    var tripToDeleteId by remember { mutableStateOf<Long?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Listen to ViewModel message events for Snackbars
    LaunchedEffect(Unit) {
        viewModel.messageEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Listen to real-time collaboration events from Firestore / Live Sync
    LaunchedEffect(Unit) {
        viewModel.syncEvents.collectLatest { event ->
            snackbarHostState.showSnackbar("⚡ Live Sync: ${event.senderName} (${event.senderRole}): ${event.message}")
        }
    }

    // Hardware back handler
    BackHandler(enabled = isSearchActive || currentDestination != AppDestination.DASHBOARD) {
        if (isSearchActive) {
            isSearchActive = false
        } else if (currentDestination != AppDestination.DASHBOARD) {
            currentDestination = AppDestination.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isSearchActive && currentDestination != AppDestination.SETTINGS) {
                TripTopBar(
                    title = if (currentDestination == AppDestination.WORKSPACE && currentTrip != null) {
                        currentTrip!!.name
                    } else "TripMate",
                    subtitle = if (currentDestination == AppDestination.WORKSPACE && currentTrip != null) {
                        currentTrip!!.destination
                    } else "Smart Travel Planner",
                    showBackButton = currentDestination == AppDestination.WORKSPACE,
                    onBackClick = {
                        currentDestination = AppDestination.DASHBOARD
                    },
                    hasReminders = true,
                    currentUser = currentUser,
                    onUserClick = { viewModel.showAuthDialog() },
                    onSearchClick = { isSearchActive = true },
                    onNotificationsClick = { showNotificationsSheet = true },
                    onSettingsClick = { currentDestination = AppDestination.SETTINGS }
                )
            }
        },
        bottomBar = {
            if (!isSearchActive) {
                TripBottomNavigationBar(
                    currentDestination = currentDestination,
                    hasActiveTrip = (selectedTripId != null || allTrips.isNotEmpty()),
                    onDestinationSelected = { dest ->
                        if (dest == AppDestination.WORKSPACE) {
                            if (selectedTripId == null && allTrips.isNotEmpty()) {
                                viewModel.selectTrip(allTrips.first().id)
                            }
                        }
                        currentDestination = dest
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isSearchActive) {
                GlobalSearchScreen(
                    trips = allTrips,
                    activities = allActivities,
                    checklist = allChecklistItems,
                    notes = allNotes,
                    onNavigateToTripTab = { tripId, tab ->
                        viewModel.selectTrip(tripId)
                        viewModel.setWorkspaceTab(tab)
                        isSearchActive = false
                        currentDestination = AppDestination.WORKSPACE
                    },
                    onBackClick = { isSearchActive = false }
                )
            } else {
                when (currentDestination) {
                    AppDestination.DASHBOARD -> {
                        DashboardScreen(
                            trips = allTrips,
                            activities = allActivities,
                            checklist = allChecklistItems,
                            notes = allNotes,
                            expenses = allExpenses,
                            currencySymbol = currencySymbol,
                            currentUser = currentUser,
                            allCollaborators = allCollaborators,
                            onCreateTripClick = {
                                tripToEdit = null
                                showCreateTripDialog = true
                            },
                            onViewTripClick = { tripId ->
                                viewModel.selectTrip(tripId)
                                currentDestination = AppDestination.WORKSPACE
                            },
                            onEditTripClick = { trip ->
                                tripToEdit = trip
                                showCreateTripDialog = true
                            },
                            onDeleteTripClick = { tripId ->
                                tripToDeleteId = tripId
                            },
                            onCollaboratorsClick = { trip ->
                                tripForCollaborators = trip
                                viewModel.selectTrip(trip.id)
                                viewModel.showCollaboratorsDialog()
                            },
                            getTripProgress = { trip ->
                                viewModel.getProgressForTrip(
                                    trip,
                                    allActivities,
                                    allChecklistItems,
                                    allNotes,
                                    allExpenses
                                )
                            }
                        )
                    }
                    AppDestination.MY_TRIPS -> {
                        TripsScreen(
                            trips = filteredTrips,
                            currentFilter = tripFilter,
                            searchQuery = tripSearchQuery,
                            currencySymbol = currencySymbol,
                            currentUser = currentUser,
                            allCollaborators = allCollaborators,
                            onFilterChange = { viewModel.setTripFilter(it) },
                            onSearchChange = { viewModel.setTripSearchQuery(it) },
                            onCreateTripClick = {
                                tripToEdit = null
                                showCreateTripDialog = true
                            },
                            onViewTripClick = { tripId ->
                                viewModel.selectTrip(tripId)
                                currentDestination = AppDestination.WORKSPACE
                            },
                            onEditTripClick = { trip ->
                                tripToEdit = trip
                                showCreateTripDialog = true
                            },
                            onDeleteTripClick = { tripId ->
                                tripToDeleteId = tripId
                            },
                            onCollaboratorsClick = { trip ->
                                tripForCollaborators = trip
                                viewModel.selectTrip(trip.id)
                                viewModel.showCollaboratorsDialog()
                            },
                            getTripProgress = { trip ->
                                viewModel.getProgressForTrip(
                                    trip,
                                    allActivities,
                                    allChecklistItems,
                                    allNotes,
                                    allExpenses
                                )
                            }
                        )
                    }
                    AppDestination.WORKSPACE -> {
                        val activeTrip = currentTrip ?: allTrips.firstOrNull()
                        if (activeTrip != null) {
                            TripWorkspaceScreen(
                                trip = activeTrip,
                                activities = currentActivities,
                                checklist = currentChecklist,
                                notes = currentNotes,
                                expenses = currentExpenses,
                                progressSummary = tripProgress,
                                currencySymbol = currencySymbol,
                                activeTab = activeWorkspaceTab,
                                currentUserRole = currentUserTripRole,
                                collaborators = currentTripCollaborators,
                                isLiveSyncConnected = isLiveSyncConnected,
                                onManageCollaboratorsClick = {
                                    tripForCollaborators = activeTrip
                                    viewModel.showCollaboratorsDialog()
                                },
                                onTabSelected = { viewModel.setWorkspaceTab(it) },
                                onBackClick = { currentDestination = AppDestination.DASHBOARD },
                                onAddActivity = { viewModel.addActivity(it) },
                                onUpdateActivity = { viewModel.updateActivity(it) },
                                onDeleteActivity = { viewModel.deleteActivity(it) },
                                onAddChecklist = { title, cat -> viewModel.addChecklistItem(activeTrip.id, title, cat) },
                                onToggleChecklist = { viewModel.toggleChecklistItem(it) },
                                onDeleteChecklist = { viewModel.deleteChecklistItem(it) },
                                onAddNote = { title, content, cat, isPinned ->
                                    viewModel.addNote(activeTrip.id, title, content, cat, isPinned)
                                },
                                onUpdateNote = { viewModel.updateNote(it) },
                                onDeleteNote = { viewModel.deleteNote(it) },
                                onTogglePinNote = { viewModel.togglePinNote(it) },
                                onAddExpense = { title, amt, cat, date, desc ->
                                    viewModel.addExpense(activeTrip.id, title, amt, cat, date, desc)
                                },
                                onDeleteExpense = { viewModel.deleteExpense(it) }
                            )
                        } else {
                            DashboardScreen(
                                trips = allTrips,
                                activities = allActivities,
                                checklist = allChecklistItems,
                                notes = allNotes,
                                expenses = allExpenses,
                                currencySymbol = currencySymbol,
                                currentUser = currentUser,
                                allCollaborators = allCollaborators,
                                onCreateTripClick = {
                                    tripToEdit = null
                                    showCreateTripDialog = true
                                },
                                onViewTripClick = { tripId ->
                                    viewModel.selectTrip(tripId)
                                    currentDestination = AppDestination.WORKSPACE
                                },
                                onEditTripClick = { trip ->
                                    tripToEdit = trip
                                    showCreateTripDialog = true
                                },
                                onDeleteTripClick = { tripId ->
                                    tripToDeleteId = tripId
                                },
                                onCollaboratorsClick = { trip ->
                                    tripForCollaborators = trip
                                    viewModel.selectTrip(trip.id)
                                    viewModel.showCollaboratorsDialog()
                                },
                                getTripProgress = { trip ->
                                    viewModel.getProgressForTrip(
                                        trip,
                                        allActivities,
                                        allChecklistItems,
                                        allNotes,
                                        allExpenses
                                    )
                                }
                            )
                        }
                    }
                    AppDestination.SETTINGS -> {
                        SettingsScreen(
                            currentCurrency = currencySymbol,
                            currentUser = currentUser,
                            isFirebaseAvailable = isFirebaseAvailable,
                            onCurrencyChange = { viewModel.setCurrency(it) },
                            onResetDemoData = {
                                viewModel.resetDemoData()
                                currentDestination = AppDestination.DASHBOARD
                            },
                            onAuthClick = { viewModel.showAuthDialog() },
                            onLogoutClick = { viewModel.signOut() },
                            onBackClick = { currentDestination = AppDestination.DASHBOARD }
                        )
                    }
                }
            }
        }
    }

    // Create / Edit Trip Dialog
    if (showCreateTripDialog) {
        CreateEditTripDialog(
            tripToEdit = tripToEdit,
            onDismiss = {
                showCreateTripDialog = false
                tripToEdit = null
            },
            onSave = { name, dest, start, end, travelers, budget, desc, cover ->
                if (tripToEdit == null) {
                    viewModel.createTrip(name, dest, start, end, travelers, budget, desc, cover)
                    currentDestination = AppDestination.WORKSPACE
                } else {
                    viewModel.updateTrip(
                        tripToEdit!!.copy(
                            name = name,
                            destination = dest,
                            startDate = start,
                            endDate = end,
                            travelers = travelers,
                            budget = budget,
                            description = desc,
                            coverImageUrl = cover.ifBlank { tripToEdit!!.coverImageUrl }
                        )
                    )
                }
                showCreateTripDialog = false
                tripToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (tripToDeleteId != null) {
        ConfirmDeleteDialog(
            title = "Delete Trip?",
            message = "Are you sure you want to delete this entire trip, including its itinerary, checklist, notes, and expenses?",
            onConfirm = {
                viewModel.deleteTrip(tripToDeleteId!!)
                tripToDeleteId = null
                if (currentDestination == AppDestination.WORKSPACE) {
                    currentDestination = AppDestination.DASHBOARD
                }
            },
            onDismiss = { tripToDeleteId = null }
        )
    }

    // Notifications & Reminders Sheet
    if (showNotificationsSheet) {
        NotificationsSheet(
            trips = allTrips,
            activities = allActivities,
            checklist = allChecklistItems,
            onDismiss = { showNotificationsSheet = false }
        )
    }

    // Authentication Dialog (Firebase Auth)
    if (isAuthDialogVisible) {
        AuthDialog(
            currentUser = currentUser,
            isFirebaseAvailable = isFirebaseAvailable,
            onDismiss = { viewModel.dismissAuthDialog() },
            onSignIn = { email, password, onResult ->
                viewModel.signIn(email, password, onResult)
            },
            onSignUp = { email, password, name, onResult ->
                viewModel.signUp(email, password, name, onResult)
            },
            onSignOut = { viewModel.signOut() },
            onSwitchUser = { user -> viewModel.switchAccount(user) }
        )
    }

    // Trip Collaboration & Live Sharing Dialog
    val activeCollabTrip = tripForCollaborators ?: currentTrip ?: allTrips.firstOrNull()
    if (isCollaboratorsDialogVisible && activeCollabTrip != null) {
        val userEmail = currentUser?.email?.lowercase() ?: ""
        val isOwner = activeCollabTrip.creatorEmail.equals(userEmail, ignoreCase = true) ||
                activeCollabTrip.creatorId == (currentUser?.uid ?: "")
        val role = if (isOwner) CollaboratorRole.OWNER else currentUserTripRole

        CollaboratorsDialog(
            trip = activeCollabTrip,
            currentUser = currentUser,
            currentUserRole = role,
            collaborators = allCollaborators.filter { it.tripId == activeCollabTrip.id },
            onDismiss = {
                viewModel.dismissCollaboratorsDialog()
                tripForCollaborators = null
            },
            onInviteCollaborator = { email, collabRole ->
                viewModel.inviteCollaborator(activeCollabTrip.id, email, collabRole)
            },
            onUpdateRole = { collab, newRole ->
                viewModel.updateCollaboratorRole(collab, newRole)
            },
            onRemoveCollaborator = { collab ->
                viewModel.removeCollaborator(collab)
            },
            onSimulateAction = {
                viewModel.simulateCollaboratorAction(activeCollabTrip.id)
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
