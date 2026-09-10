package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CollaboratorRole(val title: String, val description: String) {
    OWNER("Trip Owner", "Full access to edit trip, invite collaborators, and manage settings"),
    EDITOR("Editor", "Can add, edit, and remove activities, checklist, notes, and expenses"),
    VIEWER("Viewer", "View-only access to itinerary, map, notes, and budget")
}

@Entity(tableName = "trip_collaborators")
data class TripCollaboratorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val userId: String = "",
    val email: String,
    val displayName: String,
    val role: String = "EDITOR", // OWNER, EDITOR, VIEWER
    val status: String = "ACCEPTED", // ACCEPTED, PENDING
    val invitedAt: Long = System.currentTimeMillis()
)
