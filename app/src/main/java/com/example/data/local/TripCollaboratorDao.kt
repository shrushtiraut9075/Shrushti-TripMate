package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripCollaboratorDao {
    @Query("SELECT * FROM trip_collaborators WHERE tripId = :tripId")
    fun getCollaboratorsForTrip(tripId: Long): Flow<List<TripCollaboratorEntity>>

    @Query("SELECT * FROM trip_collaborators WHERE tripId = :tripId")
    suspend fun getCollaboratorsForTripSync(tripId: Long): List<TripCollaboratorEntity>

    @Query("SELECT * FROM trip_collaborators WHERE LOWER(email) = LOWER(:email)")
    fun getCollaborationsForEmail(email: String): Flow<List<TripCollaboratorEntity>>

    @Query("SELECT * FROM trip_collaborators WHERE LOWER(email) = LOWER(:email)")
    suspend fun getCollaborationsForEmailSync(email: String): List<TripCollaboratorEntity>

    @Query("SELECT * FROM trip_collaborators")
    fun getAllCollaborators(): Flow<List<TripCollaboratorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollaborator(collaborator: TripCollaboratorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollaborators(collaborators: List<TripCollaboratorEntity>)

    @Update
    suspend fun updateCollaborator(collaborator: TripCollaboratorEntity)

    @Query("DELETE FROM trip_collaborators WHERE id = :id")
    suspend fun deleteCollaboratorById(id: Long)

    @Query("DELETE FROM trip_collaborators WHERE tripId = :tripId")
    suspend fun deleteCollaboratorsByTripId(tripId: Long)

    @Query("DELETE FROM trip_collaborators WHERE tripId = :tripId AND LOWER(email) = LOWER(:email)")
    suspend fun removeCollaboratorByEmail(tripId: Long, email: String)
}
