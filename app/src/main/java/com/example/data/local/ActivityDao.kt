package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE tripId = :tripId ORDER BY date ASC, orderIndex ASC, startTime ASC")
    fun getActivitiesByTripId(tripId: Long): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities ORDER BY date ASC, startTime ASC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteActivityById(id: Long)

    @Query("SELECT COUNT(*) FROM activities")
    fun getTotalActivitiesCount(): Flow<Int>
}
