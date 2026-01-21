package com.example.projekatfaza23.data.repository

import android.util.Log
import com.example.projekatfaza23.UI.Role
import com.example.projekatfaza23.UI.home.Status
import com.example.projekatfaza23.data.auth.UserProfile
import com.example.projekatfaza23.data.db.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import com.example.projekatfaza23.data.db.LeaveDao

class UserRepository(private val leaveDao : LeaveDao) {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun syncUserAfterLogin(googleProfile: UserProfile) : Boolean {
        return try {
            val firestoreUser =
                firestore.collection("users").document(googleProfile.email).get().await()

            val role = firestoreUser.getString("role")
                ?: (if (googleProfile.email == "hr.app.untz@google.com") Role.Dean.name else Role.Professor.name)
            val totalDays = firestoreUser.getLong("totalDays")?.toInt() ?: 20
            val usedDays = firestoreUser.getLong("usedDays")?.toInt() ?: 0
            val empStatus = firestoreUser.getString("employeeStatus") ?: Status.AtWork.name

            val entity = UserEntity(
                email = googleProfile.email,
                firstName = googleProfile.name,
                lastName = googleProfile.lastName,
                imageUrl = googleProfile.profilePictureURL?.toString(),
                role = role,
                totalDays = totalDays,
                usedDays = usedDays,
                userStatus = empStatus
            )

            leaveDao.insertUser(entity)
            true
        } catch (e: Exception) {
            Log.e("Sync", "Error: ${e.message}")
            false
        }
    }
    fun getUser(email: String): Flow<UserEntity?> = leaveDao.getUser(email)
}