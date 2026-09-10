package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String,
    val passwordHash: String = "",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
