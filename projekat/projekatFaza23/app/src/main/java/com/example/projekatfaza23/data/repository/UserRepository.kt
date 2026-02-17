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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

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


    suspend fun updateLeaveDays(userEmail: String, daysApproved: Int): Boolean{
        return try{
         val querySnapshot = firestore.collection("user_info")
             .whereEqualTo("email", userEmail)
             .get()
             .await()

            if(!querySnapshot.isEmpty){
                val document = querySnapshot.documents[0]
                val currentTotalDays = document.getLong("totalDays")?.toInt() ?: 0
                val currentUsedDays = document.getLong("usedDays")?.toInt() ?: 0

                val newTotalDays = currentTotalDays - daysApproved
                val newUsedDays = currentUsedDays + daysApproved

                firestore.collection("user_info")
                    .document(document.id)
                    .update(
                        mapOf(
                            "totalDays" to newTotalDays,
                            "usedDays" to newUsedDays
                        )
                    ).await()
                true
            }else {
                false
            }
        }catch (e : Exception){
            Log.e("User Repository", "Greska pri smanjenju dana: ${e.message}")
            false
        }
    }
    fun realTimeUserSync(email: String) : Flow<Unit> = callbackFlow {
        val query = firestore.collection("user_info")
            .whereEqualTo("email", email)
        val listener = query.addSnapshotListener {
            snapshot, error ->
            if (error != null){
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty){
                val doc = snapshot.documents[0]
                val updatedUser = UserEntity (
                    email = email,
                    firstName = doc.getString("firstName") ?: "",
                    lastName = doc.getString("lastName") ?: "",
                    imageUrl = doc.getString("imageUrl"),
                    totalDays = doc.getLong("totalDays")?.toInt() ?: 20,
                    usedDays = doc.getLong("usedDays")?.toInt() ?: 0,
                    userStatus = doc.getString("employeeStatus") ?: "AtWork",
                    role = doc.getString("role") ?: "Professor"
                )

                launch {
                    leaveDao.insertUser(updatedUser)
                }
            }
        }
        awaitClose { listener.remove() }
    }
}