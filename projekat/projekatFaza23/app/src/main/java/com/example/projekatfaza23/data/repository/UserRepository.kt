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

            val firestoreUser = firestore.collection("user_info")
                .whereEqualTo("email", googleProfile.email)
                .get()
                .await()

            val role : String
            val totalDays: Int
            val usedDays: Int
            val userStatus: String

            if(firestoreUser.isEmpty) {
                role = if (googleProfile.email == "hr.app.untz@google.com") Role.Dean.name else Role.Professor.name
                totalDays = 20
                usedDays = 0
                userStatus = Status.AtWork.name

                val newUserMap = mapOf(
                    "email" to googleProfile.email,
                    "firstName" to googleProfile.name,
                    "lastName" to googleProfile.lastName,
                    "role" to role,
                    "totalDays" to totalDays,
                    "usedDays" to usedDays,
                    "employeeStatus" to userStatus,
                    "imageUrl" to googleProfile.profilePictureURL?.toString()
                )

                firestore.collection("user_info")
                    .add(newUserMap)
                    .await()
            } else {
                val user = firestoreUser.documents[0]

                role = user.getString("role")
                    ?: (if (googleProfile.email == "hr.app.untz@google.com") Role.Dean.name else Role.Professor.name)
                totalDays = user.getLong("totalDays")?.toInt() ?: 20
                usedDays = user.getLong("usedDays")?.toInt() ?: 0
                userStatus = user.getString("employeeStatus") ?: Status.AtWork.name
            }
            val entity = UserEntity(
                email = googleProfile.email,
                firstName = googleProfile.name,
                lastName = googleProfile.lastName,
                imageUrl = googleProfile.profilePictureURL?.toString(),
                role = role,
                totalDays = totalDays,
                usedDays = usedDays,
                userStatus = userStatus
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