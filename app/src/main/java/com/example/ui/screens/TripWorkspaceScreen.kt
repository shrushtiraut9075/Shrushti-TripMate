package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ActivityEntity
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TripEntity
import com.example.ui.components.AddChecklistDialog
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.CreateEditActivityDialog
import com.example.ui.components.CreateEditNoteDialog
import com.example.ui.components.TripProgressBar
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.viewmodel.TripProgressSummary
import com.example.ui.viewmodel.WorkspaceTab

import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sync
import com.example.data.local.CollaboratorRole
import com.example.data.local.TripCollaboratorEntity
import com.example.ui.theme.AmberWarning

@Composable
fun TripWorkspaceScreen(
    trip: TripEntity,
    activities: List<ActivityEntity>,
    checklist: List<ChecklistItemEntity>,
    notes: List<NoteEntity>,
    expenses: List<ExpenseEntity>,
    progressSummary: TripProgressSummary,
    currencySymbol: String,
    activeTab: WorkspaceTab,
    currentUserRole: CollaboratorRole = CollaboratorRole.OWNER,
    collaborators: List<TripCollaboratorEntity> = emptyList(),
    isLiveSyncConnected: Boolean = true,
    onManageCollaboratorsClick: () -> Unit = {},
    onTabSelected: (WorkspaceTab) -> Unit,
    onBackClick: () -> Unit,
    // CRUD Activity
    onAddActivity: (ActivityEntity) -> Unit,
    onUpdateActivity: (ActivityEntity) -> Unit,
    onDeleteActivity: (Long) -> Unit,
    // CRUD Checklist
    onAddChecklist: (title: String, category: String) -> Unit,
    onToggleChecklist: (ChecklistItemEntity) -> Unit,
    onDeleteChecklist: (Long) -> Unit,
    // CRUD Note
    onAddNote: (title: String, content: String, category: String, isPinned: Boolean) -> Unit,
    onUpdateNote: (NoteEntity) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onTogglePinNote: (NoteEntity) -> Unit,
    // CRUD Expense
    onAddExpense: (title: String, amount: Double, category: String, date: String, description: String) -> Unit,
    onDeleteExpense: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Dialog states
    var showAddActivityDialog by remember { mutableStateOf(false) }
    var activityToEdit by remember { mutableStateOf<ActivityEntity?>(null) }
    var activityDefaultDate by remember { mutableStateOf(trip.startDate) }

    var showAddChecklistDialog by remember { mutableStateOf(false) }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<NoteEntity?>(null) }

    var showAddExpenseDialog by remember { mutableStateOf(false) }

    var itemToDeleteId by remember { mutableStateOf<Long?>(null) }
    var deleteType by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("trip_workspace_screen")
    ) {
        // Workspace Header Banner with Back Navigation & Trip Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    if (trip.coverImageUrl == "img_hero_goa" || trip.coverImageUrl.isBlank()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_goa),
                            contentDescription = trip.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = trip.coverImageUrl,
                            contentDescription = trip.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.img_hero_goa),
                            fallback = painterResource(id = R.drawable.img_hero_goa)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    // Top Bar inside banner with Live Sync & Collaborators
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Live Sync status pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isLiveSyncConnected) EmeraldAccent.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isLiveSyncConnected) "Live Sync" else "Offline",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Collaborators Button
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Black.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable(onClick = onManageCollaboratorsClick)
                                    .testTag("workspace_collaborators_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Group,
                                        contentDescription = "Collaborators",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${collaborators.size.coerceAtLeast(1)}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (currentUserRole != CollaboratorRole.VIEWER) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.PersonAdd,
                                            contentDescription = "Invite",
                                            tint = SkyBlueLight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom info inside banner
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = trip.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SkyBlueLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = trip.destination,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SkyBlueLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${trip.startDate} – ${trip.endDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Progress Bar under banner
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    TripProgressBar(
                        progressPercent = progressSummary.overallPercent,
                        label = "Trip Planning Progress",
                        height = 6.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role & Permissions status banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (currentUserRole) {
                            CollaboratorRole.OWNER -> EmeraldAccent.copy(alpha = 0.12f)
                            CollaboratorRole.EDITOR -> SkyBluePrimary.copy(alpha = 0.12f)
                            CollaboratorRole.VIEWER -> AmberWarning.copy(alpha = 0.12f)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when (currentUserRole) {
                                    CollaboratorRole.OWNER -> "👑 You are Owner — Full edit & collaborator permissions"
                                    CollaboratorRole.EDITOR -> "✏️ Shared (Editor) — Real-time changes enabled"
                                    CollaboratorRole.VIEWER -> "👀 Shared (Viewer) — Read-only mode"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when (currentUserRole) {
                                    CollaboratorRole.OWNER -> EmeraldAccent
                                    CollaboratorRole.EDITOR -> SkyBluePrimary
                                    CollaboratorRole.VIEWER -> AmberWarning
                                }
                            )

                            Text(
                                text = "Invite +",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SkyBluePrimary,
                                modifier = Modifier
                                    .clickable(onClick = onManageCollaboratorsClick)
                                    .padding(start = 6.dp)
                            )
                        }
                    }
                }

                // Scrollable Tabs: Itinerary, Map, Checklist, Notes, Budget
                ScrollableTabRow(
                    selectedTabIndex = activeTab.ordinal,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                            color = MaterialTheme.colorScheme.primary,
                            height = 3.dp
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    WorkspaceTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { onTabSelected(tab) },
                            text = {
                                Text(
                                    text = "${tab.iconLabel} ${tab.title}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("workspace_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Active Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                WorkspaceTab.ITINERARY -> {
                    ItineraryTab(
                        trip = trip,
                        activities = activities,
                        onAddActivityClick = { defaultDate ->
                            activityDefaultDate = defaultDate
                            activityToEdit = null
                            showAddActivityDialog = true
                        },
                        onEditActivityClick = { activity ->
                            activityToEdit = activity
                            showAddActivityDialog = true
                        },
                        onDeleteActivityClick = { id ->
                            itemToDeleteId = id
                            deleteType = "ACTIVITY"
                        },
                        onReorderActivities = { /* handled in viewModel / reorder */ }
                    )
                }
                WorkspaceTab.MAP -> {
                    MapTab(
                        trip = trip,
                        activities = activities
                    )
                }
                WorkspaceTab.CHECKLIST -> {
                    ChecklistTab(
                        trip = trip,
                        checklist = checklist,
                        onToggleItem = onToggleChecklist,
                        onDeleteItem = { id ->
                            itemToDeleteId = id
                            deleteType = "CHECKLIST"
                        },
                        onAddItemClick = { showAddChecklistDialog = true }
                    )
                }
                WorkspaceTab.NOTES -> {
                    NotesTab(
                        trip = trip,
                        notes = notes,
                        onAddNoteClick = {
                            noteToEdit = null
                            showAddNoteDialog = true
                        },
                        onEditNoteClick = { note ->
                            noteToEdit = note
                            showAddNoteDialog = true
                        },
                        onDeleteNoteClick = { id ->
                            itemToDeleteId = id
                            deleteType = "NOTE"
                        },
                        onTogglePinClick = onTogglePinNote
                    )
                }
                WorkspaceTab.BUDGET -> {
                    BudgetTab(
                        trip = trip,
                        expenses = expenses,
                        currencySymbol = currencySymbol,
                        onAddExpenseClick = { showAddExpenseDialog = true },
                        onDeleteExpenseClick = { id ->
                            itemToDeleteId = id
                            deleteType = "EXPENSE"
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddActivityDialog) {
        CreateEditActivityDialog(
            tripId = trip.id,
            activityToEdit = activityToEdit,
            defaultDate = activityDefaultDate,
            onDismiss = { showAddActivityDialog = false },
            onSave = { activity ->
                if (activityToEdit == null) onAddActivity(activity)
                else onUpdateActivity(activity)
                showAddActivityDialog = false
            }
        )
    }

    if (showAddChecklistDialog) {
        AddChecklistDialog(
            onDismiss = { showAddChecklistDialog = false },
            onSave = { title, category ->
                onAddChecklist(title, category)
                showAddChecklistDialog = false
            }
        )
    }

    if (showAddNoteDialog) {
        CreateEditNoteDialog(
            tripId = trip.id,
            noteToEdit = noteToEdit,
            onDismiss = { showAddNoteDialog = false },
            onSave = { title, content, category, isPinned ->
                if (noteToEdit == null) {
                    onAddNote(title, content, category, isPinned)
                } else {
                    onUpdateNote(noteToEdit!!.copy(title = title, content = content, category = category, isPinned = isPinned))
                }
                showAddNoteDialog = false
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            currencySymbol = currencySymbol,
            defaultDate = trip.startDate,
            onDismiss = { showAddExpenseDialog = false },
            onSave = { title, amount, category, date, description ->
                onAddExpense(title, amount, category, date, description)
                showAddExpenseDialog = false
            }
        )
    }

    if (itemToDeleteId != null && deleteType != null) {
        ConfirmDeleteDialog(
            title = "Delete Item?",
            message = "Are you sure you want to delete this $deleteType item? This action cannot be undone.",
            onConfirm = {
                val id = itemToDeleteId!!
                when (deleteType) {
                    "ACTIVITY" -> onDeleteActivity(id)
                    "CHECKLIST" -> onDeleteChecklist(id)
                    "NOTE" -> onDeleteNote(id)
                    "EXPENSE" -> onDeleteExpense(id)
                }
                itemToDeleteId = null
                deleteType = null
            },
            onDismiss = {
                itemToDeleteId = null
                deleteType = null
            }
        )
    }
}
