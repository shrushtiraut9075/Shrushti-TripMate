package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items WHERE tripId = :tripId ORDER BY category ASC, id ASC")
    fun getItemsByTripId(tripId: Long): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items WHERE tripId = :tripId ORDER BY category ASC, id ASC")
    suspend fun getItemsByTripIdSync(tripId: Long): List<ChecklistItemEntity>

    @Query("SELECT * FROM checklist_items")
    fun getAllItems(): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    @Delete
    suspend fun deleteItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("UPDATE checklist_items SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, completed: Boolean)
}
