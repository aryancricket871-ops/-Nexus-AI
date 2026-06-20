package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_session")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val username: String,
    val avatarIndex: Int,
    val passwordHash: String,
    val isLoggedIn: Boolean = true,
    val proExpiry: Long = 0L,
    val lastLoginTimestamp: Long = 0L,
    val totalGenerations: Int = 0
)
