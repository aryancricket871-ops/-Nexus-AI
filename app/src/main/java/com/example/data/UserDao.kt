package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_session WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("UPDATE user_session SET isLoggedIn = 0")
    suspend fun clearSession()

    @Query("SELECT COUNT(*) FROM user_session")
    fun getTotalUsers(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_session WHERE lastLoginTimestamp > :timestamp")
    fun getActiveUsers(timestamp: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalGenerations), 0) FROM user_session")
    fun getTotalGenerations(): Flow<Int>

    @Query("UPDATE user_session SET proExpiry = :expiry WHERE username = :username")
    suspend fun grantPro(username: String, expiry: Long)
}
