package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val budget: Double,
    val description: String,
    val coverImageUrl: String = "",
    val status: String = "UPCOMING", // UPCOMING, ONGOING, COMPLETED
    val creatorId: String = "alex_carter_01",
    val creatorEmail: String = "alex@travel.com",
    val createdAt: Long = System.currentTimeMillis()
)
