package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AppUser
import com.example.data.auth.AuthResult
import com.example.data.auth.AuthService
import com.example.data.local.ActivityEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.CollaboratorRole
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TripCollaboratorEntity
import com.example.data.local.TripEntity
import com.example.data.repository.TripRepository
import com.example.data.sync.RealtimeSyncService
import com.example.data.sync.TripSyncEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class WorkspaceTab(val title: String, val iconLabel: String) {
    ITINERARY("Itinerary", "🗓"),
    MAP("Map View", "🗺"),
    CHECKLIST("Checklist", "✅"),
    NOTES("Notes", "📝"),
    BUDGET("Budget", "💰")
}

enum class TripFilter(val label: String) {
    ALL("All Trips"),
    MY_TRIPS("Created By Me"),
    SHARED("Shared With Me"),
    UPCOMING("Upcoming"),
    ONGOING("Ongoing"),
    COMPLETED("Completed")
}

data class TripProgressSummary(
    val itineraryPercent: Int,
    val checklistPercent: Int,
    val notesPercent: Int,
    val budgetPercent: Int,
    val overallPercent: Int
)

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository: TripRepository = TripRepository(database)
    private val authService = AuthService(application, database.userDao(), viewModelScope)
    private val realtimeSyncService = RealtimeSyncService(
        application,
        database.activityDao(),
        database.checklistDao(),
        database.noteDao(),
        viewModelScope
    )

    init {
        viewModelScope.launch {
            repository.ensureSampleDataLoaded()
        }
    }

    // Auth state & User management
    val currentUser: StateFlow<AppUser?> = authService.currentUser
    val isFirebaseAvailable: StateFlow<Boolean> = authService.isFirebaseAvailable

    private val _isAuthDialogVisible = MutableStateFlow(false)
    val isAuthDialogVisible: StateFlow<Boolean> = _isAuthDialogVisible.asStateFlow()

    fun showAuthDialog() {
        _isAuthDialogVisible.value = true
    }

    fun dismissAuthDialog() {
        _isAuthDialogVisible.value = false
    }

    private val _isCollaboratorsDialogVisible = MutableStateFlow(false)
    val isCollaboratorsDialogVisible: StateFlow<Boolean> = _isCollaboratorsDialogVisible.asStateFlow()

    fun showCollaboratorsDialog() {
        _isCollaboratorsDialogVisible.value = true
    }

    fun dismissCollaboratorsDialog() {
        _isCollaboratorsDialogVisible.value = false
    }

    // Real-time synchronization events & connection
    val syncEvents: SharedFlow<TripSyncEvent> = realtimeSyncService.syncEvents
    val isLiveSyncConnected: StateFlow<Boolean> = realtimeSyncService.isLiveSyncConnected

    // Snackbar event channel
    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    // Currency symbol preference
    private val _currency = MutableStateFlow("₹")
    val currency: StateFlow<String> = _currency.asStateFlow()

    fun setCurrency(newCurrency: String) {
        _currency.value = newCurrency
    }

    // Navigation & Workspace State
    private val _selectedTripId = MutableStateFlow<Long?>(null)
    val selectedTripId: StateFlow<Long?> = _selectedTripId.asStateFlow()

    private val _activeWorkspaceTab = MutableStateFlow(WorkspaceTab.ITINERARY)
    val activeWorkspaceTab: StateFlow<WorkspaceTab> = _activeWorkspaceTab.asStateFlow()

    fun selectTrip(tripId: Long?) {
        _selectedTripId.value = tripId
        if (tripId != null) {
            realtimeSyncService.startListeningToTrip(tripId)
        } else {
            realtimeSyncService.stopListening()
        }
    }

    fun setWorkspaceTab(tab: WorkspaceTab) {
        _activeWorkspaceTab.value = tab
    }

    // Trips list & Filter
    val allTrips: StateFlow<List<TripEntity>> = repository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCollaborators: StateFlow<List<TripCollaboratorEntity>> = repository.allCollaborators
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _tripFilter = MutableStateFlow(TripFilter.ALL)
    val tripFilter: StateFlow<TripFilter> = _tripFilter.asStateFlow()

    fun setTripFilter(filter: TripFilter) {
        _tripFilter.value = filter
    }

    private val _tripSearchQuery = MutableStateFlow("")
    val tripSearchQuery: StateFlow<String> = _tripSearchQuery.asStateFlow()

    fun setTripSearchQuery(query: String) {
        _tripSearchQuery.value = query
    }

    val filteredTrips: StateFlow<List<TripEntity>> = combine(
        allTrips,
        _tripFilter,
        _tripSearchQuery,
        currentUser,
        allCollaborators
    ) { trips, filter, query, user, collaborators ->
        val userEmail = user?.email?.lowercase() ?: ""
        val userId = user?.uid ?: ""

        trips.filter { trip ->
            val isOwner = trip.creatorEmail.equals(userEmail, ignoreCase = true) || trip.creatorId == userId
            val isCollaborator = collaborators.any {
                it.tripId == trip.id && it.email.equals(userEmail, ignoreCase = true)
            }

            val matchesFilter = when (filter) {
                TripFilter.ALL -> true
                TripFilter.MY_TRIPS -> isOwner
                TripFilter.SHARED -> !isOwner && isCollaborator
                TripFilter.UPCOMING -> trip.status.equals("UPCOMING", ignoreCase = true)
                TripFilter.ONGOING -> trip.status.equals("ONGOING", ignoreCase = true)
                TripFilter.COMPLETED -> trip.status.equals("COMPLETED", ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    trip.name.contains(query, ignoreCase = true) ||
                    trip.destination.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Trip details
    val currentTrip: StateFlow<TripEntity?> = _selectedTripId.flatMapLatest { id ->
        if (id != null) repository.getTripById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Collaborators for current trip
    val currentTripCollaborators: StateFlow<List<TripCollaboratorEntity>> = _selectedTripId.flatMapLatest { id ->
        if (id != null) repository.getCollaboratorsForTrip(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current User's Role for Selected Trip
    val currentUserTripRole: StateFlow<CollaboratorRole> = combine(
        currentTrip,
        currentTripCollaborators,
        currentUser
    ) { trip, collaborators, user ->
        if (trip == null || user == null) {
            CollaboratorRole.VIEWER
        } else if (trip.creatorEmail.equals(user.email, ignoreCase = true) || trip.creatorId == user.uid) {
            CollaboratorRole.OWNER
        } else {
            val collab = collaborators.firstOrNull { it.email.equals(user.email, ignoreCase = true) }
            when (collab?.role?.uppercase()) {
                "OWNER" -> CollaboratorRole.OWNER
                "EDITOR" -> CollaboratorRole.EDITOR
                else -> CollaboratorRole.VIEWER
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CollaboratorRole.VIEWER)

    val canEditCurrentTrip: StateFlow<Boolean> = combine(currentUserTripRole) { (role) ->
        role == CollaboratorRole.OWNER || role == CollaboratorRole.EDITOR
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isOwnerOfCurrentTrip: StateFlow<Boolean> = combine(currentUserTripRole) { (role) ->
        role == CollaboratorRole.OWNER
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Current Trip Activities
    val currentActivities: StateFlow<List<ActivityEntity>> = _selectedTripId.flatMapLatest { id ->
        if (id != null) repository.getActivitiesForTrip(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Trip Checklist
    val currentChecklist: StateFlow<List<ChecklistItemEntity>> = _selectedTripId.flatMapLatest { id ->
        if (id != null) repository.getChecklistForTrip(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Trip Notes
    val currentNotes: StateFlow<List<NoteEntity>> = _selectedTripId.flatMapLatest { id ->
        if (id != null) repository.getNotesForTrip(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Trip Expenses
    val currentExpenses: StateFlow<List<ExpenseEntity>> = _selectedTripId.flatMapLatest { id ->
        if (id != null) repository.getExpensesForTrip(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All activities across trips (for dashboard & global search)
    val allActivities: StateFlow<List<ActivityEntity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChecklistItems: StateFlow<List<ChecklistItemEntity>> = repository.allChecklistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Trip Progress Calculation
    val tripProgress: StateFlow<TripProgressSummary> = combine(
        currentTrip,
        currentActivities,
        currentChecklist,
        currentNotes,
        currentExpenses
    ) { trip, activities, checklist, notes, expenses ->
        if (trip == null) {
            TripProgressSummary(0, 0, 0, 0, 0)
        } else {
            val itineraryPct = (activities.size * 15).coerceIn(0, 100)
            val checklistPct = if (checklist.isNotEmpty()) {
                val done = checklist.count { it.isCompleted }
                ((done.toDouble() / checklist.size) * 100).toInt()
            } else 0
            val notesPct = (notes.size * 25).coerceIn(0, 100)
            val totalSpent = expenses.sumOf { it.amount }
            val budgetPct = if (trip.budget > 0) {
                ((totalSpent / trip.budget) * 100).toInt().coerceIn(0, 100)
            } else 0

            val overall = ((itineraryPct * 0.35) + (checklistPct * 0.30) + (notesPct * 0.15) + (budgetPct * 0.20)).toInt()
                .coerceIn(0, 100)

            TripProgressSummary(
                itineraryPercent = itineraryPct,
                checklistPercent = checklistPct,
                notesPercent = notesPct,
                budgetPercent = budgetPct,
                overallPercent = overall
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TripProgressSummary(0, 0, 0, 0, 0))

    // Helper for any trip's overall progress (for trip cards)
    fun getProgressForTrip(trip: TripEntity, acts: List<ActivityEntity>, checks: List<ChecklistItemEntity>, nts: List<NoteEntity>, exps: List<ExpenseEntity>): Int {
        val tripActs = acts.filter { it.tripId == trip.id }
        val tripChecks = checks.filter { it.tripId == trip.id }
        val tripNts = nts.filter { it.tripId == trip.id }
        val tripExps = exps.filter { it.tripId == trip.id }

        val itineraryPct = (tripActs.size * 15).coerceIn(0, 100)
        val checklistPct = if (tripChecks.isNotEmpty()) {
            val done = tripChecks.count { it.isCompleted }
            ((done.toDouble() / tripChecks.size) * 100).toInt()
        } else 0
        val notesPct = (tripNts.size * 25).coerceIn(0, 100)
        val totalSpent = tripExps.sumOf { it.amount }
        val budgetPct = if (trip.budget > 0) ((totalSpent / trip.budget) * 100).toInt().coerceIn(0, 100) else 0

        return ((itineraryPct * 0.35) + (checklistPct * 0.30) + (notesPct * 0.15) + (budgetPct * 0.20)).toInt()
            .coerceIn(0, 100)
    }

    // Authentication Operations
    fun signUp(email: String, pass: String, name: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val res = authService.signUp(email, pass, name)) {
                is AuthResult.Success -> {
                    _messageEvent.emit(res.message)
                    _isAuthDialogVisible.value = false
                    onResult(true, res.message)
                }
                is AuthResult.Error -> {
                    _messageEvent.emit(res.message)
                    onResult(false, res.message)
                }
            }
        }
    }

    fun signIn(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val res = authService.signIn(email, pass)) {
                is AuthResult.Success -> {
                    _messageEvent.emit(res.message)
                    _isAuthDialogVisible.value = false
                    onResult(true, res.message)
                }
                is AuthResult.Error -> {
                    _messageEvent.emit(res.message)
                    onResult(false, res.message)
                }
            }
        }
    }

    fun signOut() {
        authService.signOut()
        viewModelScope.launch {
            _messageEvent.emit("Logged out successfully.")
        }
    }

    fun switchAccount(user: AppUser) {
        authService.switchAccount(user)
        viewModelScope.launch {
            _messageEvent.emit("Switched to ${user.displayName} (${user.email})")
        }
    }

    // Collaboration Operations
    fun inviteCollaborator(tripId: Long, email: String, role: String, displayName: String = "") {
        viewModelScope.launch {
            val cleanEmail = email.trim()
            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                _messageEvent.emit("Please enter a valid collaborator email.")
                return@launch
            }

            val cleanName = displayName.ifBlank { cleanEmail.substringBefore("@") }
            val existing = repository.getCollaboratorsForTripSync(tripId)
            if (existing.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
                _messageEvent.emit("$cleanEmail is already a collaborator on this trip.")
                return@launch
            }

            val newCollaborator = TripCollaboratorEntity(
                tripId = tripId,
                email = cleanEmail,
                displayName = cleanName,
                role = role,
                status = "ACCEPTED"
            )
            repository.addCollaborator(newCollaborator)

            val senderName = currentUser.value?.displayName ?: "Trip Creator"
            val broadcastMsg = "$senderName invited $cleanName as $role"
            realtimeSyncService.broadcastLocalEdit(tripId, senderName, "Owner", broadcastMsg)
            _messageEvent.emit("Invited $cleanName ($role) successfully!")
        }
    }

    fun updateCollaboratorRole(collaborator: TripCollaboratorEntity, newRole: String) {
        viewModelScope.launch {
            repository.updateCollaborator(collaborator.copy(role = newRole))
            val senderName = currentUser.value?.displayName ?: "Trip Owner"
            realtimeSyncService.broadcastLocalEdit(
                collaborator.tripId,
                senderName,
                "Owner",
                "Updated ${collaborator.displayName}'s role to $newRole"
            )
            _messageEvent.emit("Role updated for ${collaborator.displayName}")
        }
    }

    fun removeCollaborator(collaborator: TripCollaboratorEntity) {
        viewModelScope.launch {
            repository.removeCollaborator(collaborator.id)
            val senderName = currentUser.value?.displayName ?: "Trip Owner"
            realtimeSyncService.broadcastLocalEdit(
                collaborator.tripId,
                senderName,
                "Owner",
                "Removed ${collaborator.displayName} from collaborators"
            )
            _messageEvent.emit("Collaborator removed")
        }
    }

    fun simulateCollaboratorAction(tripId: Long) {
        viewModelScope.launch {
            realtimeSyncService.simulateCollaboratorAction(tripId)
        }
    }

    // CRUD Operations - Trip
    fun createTrip(
        name: String,
        destination: String,
        startDate: String,
        endDate: String,
        travelers: Int,
        budget: Double,
        description: String,
        coverImageUrl: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val creatorUid = user?.uid ?: "alex_carter_01"
            val creatorEmail = user?.email ?: "alex@travel.com"

            val newTrip = TripEntity(
                name = name,
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                travelers = travelers,
                budget = budget,
                description = description,
                coverImageUrl = coverImageUrl.ifBlank { "img_hero_goa" },
                creatorId = creatorUid,
                creatorEmail = creatorEmail
            )
            val newId = repository.insertTrip(newTrip)

            // Add creator as OWNER collaborator
            repository.addCollaborator(
                TripCollaboratorEntity(
                    tripId = newId,
                    userId = creatorUid,
                    email = creatorEmail,
                    displayName = user?.displayName ?: "Trip Creator",
                    role = "OWNER",
                    status = "ACCEPTED"
                )
            )

            _selectedTripId.value = newId
            _activeWorkspaceTab.value = WorkspaceTab.ITINERARY
            realtimeSyncService.startListeningToTrip(newId)
            _messageEvent.emit("Trip \"$name\" created successfully!")
        }
    }

    fun updateTrip(trip: TripEntity) {
        viewModelScope.launch {
            repository.updateTrip(trip)
            val sender = currentUser.value?.displayName ?: "User"
            realtimeSyncService.broadcastLocalEdit(trip.id, sender, currentUserTripRole.value.title, "Updated trip overview")
            _messageEvent.emit("Trip details updated!")
        }
    }

    fun deleteTrip(tripId: Long) {
        viewModelScope.launch {
            repository.deleteTripById(tripId)
            if (_selectedTripId.value == tripId) {
                _selectedTripId.value = null
            }
            _messageEvent.emit("Trip deleted")
        }
    }

    // CRUD Operations - Activity
    fun addActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
            val sender = currentUser.value?.displayName ?: "User"
            realtimeSyncService.broadcastLocalEdit(activity.tripId, sender, currentUserTripRole.value.title, "Added activity: ${activity.name}")
            _messageEvent.emit("Activity added: ${activity.name}")
        }
    }

    fun updateActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.updateActivity(activity)
            val sender = currentUser.value?.displayName ?: "User"
            realtimeSyncService.broadcastLocalEdit(activity.tripId, sender, currentUserTripRole.value.title, "Updated activity: ${activity.name}")
            _messageEvent.emit("Activity updated")
        }
    }

    fun deleteActivity(activityId: Long) {
        viewModelScope.launch {
            repository.deleteActivityById(activityId)
            _messageEvent.emit("Activity removed")
        }
    }

    // CRUD Operations - Checklist
    fun addChecklistItem(tripId: Long, title: String, category: String) {
        viewModelScope.launch {
            repository.insertChecklistItem(
                ChecklistItemEntity(
                    tripId = tripId,
                    title = title,
                    category = category,
                    isCompleted = false
                )
            )
            val sender = currentUser.value?.displayName ?: "User"
            realtimeSyncService.broadcastLocalEdit(tripId, sender, currentUserTripRole.value.title, "Added checklist item: $title")
            _messageEvent.emit("Task added to $category")
        }
    }

    fun toggleChecklistItem(item: ChecklistItemEntity) {
        viewModelScope.launch {
            val newStatus = !item.isCompleted
            repository.toggleChecklistItem(item.id, newStatus)
            val sender = currentUser.value?.displayName ?: "User"
            val statusText = if (newStatus) "completed" else "uncompleted"
            realtimeSyncService.broadcastLocalEdit(item.tripId, sender, currentUserTripRole.value.title, "$statusText task: ${item.title}")
        }
    }

    fun deleteChecklistItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteChecklistItemById(itemId)
            _messageEvent.emit("Task removed")
        }
    }

    // CRUD Operations - Note
    fun addNote(tripId: Long, title: String, content: String, category: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    tripId = tripId,
                    title = title,
                    content = content,
                    category = category,
                    isPinned = isPinned
                )
            )
            val sender = currentUser.value?.displayName ?: "User"
            realtimeSyncService.broadcastLocalEdit(tripId, sender, currentUserTripRole.value.title, "Added note: $title")
            _messageEvent.emit("Note added")
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
            val sender = currentUser.value?.displayName ?: "User"
            realtimeSyncService.broadcastLocalEdit(note.tripId, sender, currentUserTripRole.value.title, "Updated note: ${note.title}")
            _messageEvent.emit("Note updated")
        }
    }

    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.togglePinNote(note.id, !note.isPinned)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.deleteNoteById(noteId)
            _messageEvent.emit("Note deleted")
        }
    }

    // CRUD Operations - Expense
    fun addExpense(tripId: Long, title: String, amount: Double, category: String, date: String, description: String) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    tripId = tripId,
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    description = description
                )
            )
            val sender = currentUser.value?.displayName ?: "User"
            realtimeSyncService.broadcastLocalEdit(tripId, sender, currentUserTripRole.value.title, "Logged expense: $title (${_currency.value}$amount)")
            _messageEvent.emit("Expense added")
        }
    }

    fun deleteExpense(expenseId: Long) {
        viewModelScope.launch {
            repository.deleteExpenseById(expenseId)
            _messageEvent.emit("Expense deleted")
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            _selectedTripId.value = 1L
            realtimeSyncService.startListeningToTrip(1L)
            _messageEvent.emit("Sample trip data restored!")
        }
    }
}
