package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activities",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tripId"])]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val name: String,
    val date: String, // e.g., "15 Oct 2026"
    val startTime: String, // e.g., "09:00 AM"
    val endTime: String, // e.g., "11:00 AM"
    val location: String,
    val category: String, // Transport, Hotel, Food, Sightseeing, Shopping, Adventure, Other
    val description: String,
    val orderIndex: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
