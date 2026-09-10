package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AppUser
import com.example.data.local.CollaboratorRole
import com.example.data.local.TripCollaboratorEntity
import com.example.data.local.TripEntity
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.SkyBluePrimary

@Composable
fun CollaboratorsDialog(
    trip: TripEntity,
    currentUser: AppUser?,
    currentUserRole: CollaboratorRole,
    collaborators: List<TripCollaboratorEntity>,
    onDismiss: () -> Unit,
    onInviteCollaborator: (String, String) -> Unit,
    onUpdateRole: (TripCollaboratorEntity, String) -> Unit,
    onRemoveCollaborator: (TripCollaboratorEntity) -> Unit,
    onSimulateAction: () -> Unit
) {
    var inviteEmail by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("EDITOR") } // EDITOR or VIEWER
    val isOwner = currentUserRole == CollaboratorRole.OWNER

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Collaborators",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Trip Collaboration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Real-time Collaboration Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldAccent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Real-time Live Sync active across shared members",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = EmeraldAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Invite Section (Only for Owner or Editors)
                if (currentUserRole != CollaboratorRole.VIEWER) {
                    Text(
                        text = "Invite Collaborator",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        placeholder = { Text("colleague@example.com") },
                        label = { Text("User Email") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invite_email_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selectedRole == "EDITOR",
                            onClick = { selectedRole = "EDITOR" },
                            label = { Text("Editor (Can edit)") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("chip_role_editor")
                        )
                        FilterChip(
                            selected = selectedRole == "VIEWER",
                            onClick = { selectedRole = "VIEWER" },
                            label = { Text("Viewer (Read-only)") },
                            leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("chip_role_viewer")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (inviteEmail.isNotBlank()) {
                                onInviteCollaborator(inviteEmail, selectedRole)
                                inviteEmail = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_invite_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Collaboration Invite")
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberWarning.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "You are currently in Viewer mode. Only the trip owner and editors can invite collaborators.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Collaborator List
                Text(
                    text = "Members (${collaborators.size}):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                ) {
                    items(collaborators, key = { it.id }) { collab ->
                        CollaboratorItemRow(
                            collab = collab,
                            isOwner = isOwner,
                            currentUserEmail = currentUser?.email ?: "",
                            onUpdateRole = { onUpdateRole(collab, it) },
                            onRemove = { onRemoveCollaborator(collab) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Real-time Live Action Simulator Box
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Realtime Update",
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Simulate Real-Time Update",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyBluePrimary
                                )
                            }
                            Text(
                                text = "Simulates remote collaborator edits instantly in real time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onSimulateAction,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyBluePrimary
                            ),
                            modifier = Modifier.testTag("simulate_realtime_button")
                        ) {
                            Text("Simulate", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun CollaboratorItemRow(
    collab: TripCollaboratorEntity,
    isOwner: Boolean,
    currentUserEmail: String,
    onUpdateRole: (String) -> Unit,
    onRemove: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isSelf = collab.email.equals(currentUserEmail, ignoreCase = true)
    val isTripOwner = collab.role.equals("OWNER", ignoreCase = true)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when (collab.role.uppercase()) {
                                "OWNER" -> EmeraldAccent
                                "EDITOR" -> SkyBluePrimary
                                else -> MaterialTheme.colorScheme.secondary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = collab.displayName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = collab.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isSelf) {
                            Text(
                                text = " (You)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = collab.email,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Role Chip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (collab.role.uppercase()) {
                    "OWNER" -> EmeraldAccent.copy(alpha = 0.15f)
                    "EDITOR" -> SkyBluePrimary.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.clickable(enabled = isOwner && !isTripOwner) {
                    menuExpanded = true
                }
            ) {
                Text(
                    text = collab.role.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (collab.role.uppercase()) {
                        "OWNER" -> EmeraldAccent
                        "EDITOR" -> SkyBluePrimary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Make Editor") },
                        onClick = {
                            onUpdateRole("EDITOR")
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Make Viewer") },
                        onClick = {
                            onUpdateRole("VIEWER")
                            menuExpanded = false
                        }
                    )
                }
            }

            if (isOwner && !isTripOwner) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Collaborator",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
