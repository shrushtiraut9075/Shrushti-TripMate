package com.example.data.repository

import com.example.data.local.ActivityDao
import com.example.data.local.ActivityEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ChecklistDao
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.ExpenseDao
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteDao
import com.example.data.local.NoteEntity
import com.example.data.local.SampleData
import com.example.data.local.TripDao
import com.example.data.local.TripEntity
import com.example.data.local.TripCollaboratorDao
import com.example.data.local.TripCollaboratorEntity
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TripRepository(
    private val database: AppDatabase,
    private val tripDao: TripDao = database.tripDao(),
    private val activityDao: ActivityDao = database.activityDao(),
    private val checklistDao: ChecklistDao = database.checklistDao(),
    private val noteDao: NoteDao = database.noteDao(),
    private val expenseDao: ExpenseDao = database.expenseDao(),
    private val collaboratorDao: TripCollaboratorDao = database.collaboratorDao(),
    private val userDao: UserDao = database.userDao()
) {
    val allTrips: Flow<List<TripEntity>> = tripDao.getAllTrips()
    val allActivities: Flow<List<ActivityEntity>> = activityDao.getAllActivities()
    val allChecklistItems: Flow<List<ChecklistItemEntity>> = checklistDao.getAllItems()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allCollaborators: Flow<List<TripCollaboratorEntity>> = collaboratorDao.getAllCollaborators()

    fun getTripById(id: Long): Flow<TripEntity?> = tripDao.getTripById(id)

    fun getActivitiesForTrip(tripId: Long): Flow<List<ActivityEntity>> =
        activityDao.getActivitiesByTripId(tripId)

    fun getChecklistForTrip(tripId: Long): Flow<List<ChecklistItemEntity>> =
        checklistDao.getItemsByTripId(tripId)

    fun getNotesForTrip(tripId: Long): Flow<List<NoteEntity>> =
        noteDao.getNotesByTripId(tripId)

    fun getExpensesForTrip(tripId: Long): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesByTripId(tripId)

    fun getCollaboratorsForTrip(tripId: Long): Flow<List<TripCollaboratorEntity>> =
        collaboratorDao.getCollaboratorsForTrip(tripId)

    suspend fun getCollaboratorsForTripSync(tripId: Long): List<TripCollaboratorEntity> =
        collaboratorDao.getCollaboratorsForTripSync(tripId)

    fun getCollaborationsForEmail(email: String): Flow<List<TripCollaboratorEntity>> =
        collaboratorDao.getCollaborationsForEmail(email)

    suspend fun getCollaborationsForEmailSync(email: String): List<TripCollaboratorEntity> =
        collaboratorDao.getCollaborationsForEmailSync(email)

    // User operations
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    // Collaborator operations
    suspend fun addCollaborator(collaborator: TripCollaboratorEntity): Long =
        collaboratorDao.insertCollaborator(collaborator)

    suspend fun updateCollaborator(collaborator: TripCollaboratorEntity) =
        collaboratorDao.updateCollaborator(collaborator)

    suspend fun removeCollaborator(id: Long) = collaboratorDao.deleteCollaboratorById(id)
    suspend fun removeCollaboratorByEmail(tripId: Long, email: String) =
        collaboratorDao.removeCollaboratorByEmail(tripId, email)

    suspend fun ensureSampleDataLoaded() {
        val trips = tripDao.getAllTrips().first()
        if (trips.isEmpty()) {
            AppDatabase.populateSampleData(database)
        }
    }

    suspend fun resetToSampleData() {
        tripDao.clearAll()
        AppDatabase.populateSampleData(database)
    }

    // Trip operations
    suspend fun insertTrip(trip: TripEntity): Long = tripDao.insertTrip(trip)
    suspend fun updateTrip(trip: TripEntity) = tripDao.updateTrip(trip)
    suspend fun deleteTrip(trip: TripEntity) = tripDao.deleteTrip(trip)
    suspend fun deleteTripById(id: Long) = tripDao.deleteTripById(id)

    // Activity operations
    suspend fun insertActivity(activity: ActivityEntity): Long = activityDao.insertActivity(activity)
    suspend fun updateActivity(activity: ActivityEntity) = activityDao.updateActivity(activity)
    suspend fun deleteActivity(activity: ActivityEntity) = activityDao.deleteActivity(activity)
    suspend fun deleteActivityById(id: Long) = activityDao.deleteActivityById(id)

    // Checklist operations
    suspend fun insertChecklistItem(item: ChecklistItemEntity): Long = checklistDao.insertItem(item)
    suspend fun updateChecklistItem(item: ChecklistItemEntity) = checklistDao.updateItem(item)
    suspend fun toggleChecklistItem(id: Long, completed: Boolean) =
        checklistDao.updateCompletionStatus(id, completed)
    suspend fun deleteChecklistItem(item: ChecklistItemEntity) = checklistDao.deleteItem(item)
    suspend fun deleteChecklistItemById(id: Long) = checklistDao.deleteItemById(id)

    // Notes operations
    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)
    suspend fun togglePinNote(id: Long, isPinned: Boolean) = noteDao.togglePin(id, isPinned)

    // Expense operations
    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)
    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteExpenseById(id)
}
