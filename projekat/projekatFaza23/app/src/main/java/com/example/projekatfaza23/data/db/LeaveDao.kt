package com.example.projekatfaza23.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUser(email: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<LeaveRequestEntity>)

    @Query("SELECT * FROM leave_request WHERE userEmail = :email ORDER BY createdAt DESC")
    fun getRequestsForUser(email: String): Flow<List<LeaveRequestEntity>>

    @Query("SELECT MAX(createdAt) FROM leave_request WHERE userEmail = :email")
    suspend fun getLastTimestamp(email: String): Long?

    @Query(
        """
    DELETE FROM leave_request WHERE id IN (SELECT id FROM leave_request WHERE userEmail = :email 
        ORDER BY createdAt DESC LIMIT -1 OFFSET 30)"""
    )
    suspend fun trimOldRequests(email: String)

    @Query("UPDATE users SET userStatus = :newStatus WHERE email = :email")
    suspend fun updateUserStatus(email: String, newStatus: String)
//
//    @Query("SELECT userStatus FROM users WHERE email = :email")
//    suspend fun getUserStatus(email: String, newStatus: String)

    @Query("DELETE FROM leave_request WHERE id = :id")
    suspend fun cancelRequest(id: String)
}