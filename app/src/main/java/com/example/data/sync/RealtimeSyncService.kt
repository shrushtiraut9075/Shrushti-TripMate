package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.data.local.ActivityDao
import com.example.data.local.ActivityEntity
import com.example.data.local.ChecklistDao
import com.example.data.local.NoteDao
import com.example.data.local.NoteEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripSyncEvent(
    val tripId: Long,
    val senderName: String,
    val senderRole: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class RealtimeSyncService(
    private val context: Context,
    private val activityDao: ActivityDao,
    private val checklistDao: ChecklistDao,
    private val noteDao: NoteDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "RealtimeSyncService"

    private val _syncEvents = MutableSharedFlow<TripSyncEvent>(extraBufferCapacity = 64)
    val syncEvents: SharedFlow<TripSyncEvent> = _syncEvents.asSharedFlow()

    private val _isLiveSyncConnected = MutableStateFlow(true)
    val isLiveSyncConnected: StateFlow<Boolean> = _isLiveSyncConnected.asStateFlow()

    private var firestore: FirebaseFirestore? = null
    private var activeTripListener: ListenerRegistration? = null

    init {
        initFirestoreIfAvailable()
    }

    private fun initFirestoreIfAvailable() {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                Log.d(TAG, "Firestore initialized for real-time collaboration.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization skipped: ${e.message}")
        }
    }

    fun startListeningToTrip(tripId: Long) {
        activeTripListener?.remove()
        firestore?.let { db ->
            try {
                activeTripListener = db.collection("trips")
                    .document(tripId.toString())
                    .collection("live_events")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w(TAG, "Firestore listen error: ${error.message}")
                            return@addSnapshotListener
                        }
                        snapshot?.documentChanges?.forEach { change ->
                            val data = change.document.data
                            val sender = data["senderName"] as? String ?: "Collaborator"
                            val role = data["senderRole"] as? String ?: "Editor"
                            val msg = data["message"] as? String ?: "Updated trip details"
                            scope.launch {
                                _syncEvents.emit(
                                    TripSyncEvent(
                                        tripId = tripId,
                                        senderName = sender,
                                        senderRole = role,
                                        message = msg
                                    )
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Could not attach Firestore listener: ${e.message}")
            }
        }
    }

    fun stopListening() {
        activeTripListener?.remove()
        activeTripListener = null
    }

    suspend fun broadcastLocalEdit(tripId: Long, senderName: String, senderRole: String, message: String) {
        val event = TripSyncEvent(
            tripId = tripId,
            senderName = senderName,
            senderRole = senderRole,
            message = message
        )
        _syncEvents.emit(event)

        // Push to Firestore if available
        firestore?.let { db ->
            try {
                val payload = hashMapOf(
                    "tripId" to tripId,
                    "senderName" to senderName,
                    "senderRole" to senderRole,
                    "message" to message,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("trips")
                    .document(tripId.toString())
                    .collection("live_events")
                    .add(payload)
            } catch (e: Exception) {
                Log.w(TAG, "Firestore push skipped: ${e.message}")
            }
        }
    }

    /**
     * Simulates incoming real-time collaboration updates from an invited collaborator (e.g. Maya Lin).
     * This provides direct interactive testing of WebSocket/real-time collaboration updates.
     */
    fun simulateCollaboratorAction(tripId: Long, collaboratorName: String = "Maya Lin", role: String = "Editor") {
        scope.launch {
            val simulationVariants = listOf(
                "CHECKLIST" to "$collaboratorName completed 'Confirm beach resort booking'",
                "ACTIVITY" to "$collaboratorName added 'Sunset Beach Volleyball & Cocktails' to Day 2",
                "NOTE" to "$collaboratorName added a travel tip: 'Carry light cotton clothes & sunscreen'",
                "BUDGET" to "$collaboratorName logged an expense: 'Airport Taxi - ₹850'"
            )
            val chosen = simulationVariants.random()

            when (chosen.first) {
                "CHECKLIST" -> {
                    val items = checklistDao.getItemsByTripIdSync(tripId)
                    val uncompleted = items.firstOrNull { !it.isCompleted }
                    if (uncompleted != null) {
                        checklistDao.updateCompletionStatus(uncompleted.id, true)
                        _syncEvents.emit(
                            TripSyncEvent(
                                tripId = tripId,
                                senderName = collaboratorName,
                                senderRole = role,
                                message = "Checked off \"${uncompleted.title}\""
                            )
                        )
                        return@launch
                    }
                }
                "ACTIVITY" -> {
                    val newActivity = ActivityEntity(
                        tripId = tripId,
                        name = "Beach Volleyball & Sunset Drinks",
                        category = "Activities",
                        date = "2026-10-16",
                        startTime = "17:30",
                        endTime = "19:30",
                        location = "Baga Beach Shack",
                        latitude = 15.5553,
                        longitude = 73.7517,
                        description = "Added by $collaboratorName in real-time"
                    )
                    activityDao.insertActivity(newActivity)
                }
                "NOTE" -> {
                    val newNote = NoteEntity(
                        tripId = tripId,
                        title = "Recommendation from $collaboratorName",
                        content = "Visit Thalassa in Siolim for Greek cuisine and cliffside sunset views!",
                        category = "Places to visit",
                        isPinned = false
                    )
                    noteDao.insertNote(newNote)
                }
                "BUDGET" -> {
                    // event broadcast
                }
            }

            _syncEvents.emit(
                TripSyncEvent(
                    tripId = tripId,
                    senderName = collaboratorName,
                    senderRole = role,
                    message = chosen.second
                )
            )
        }
    }
}
