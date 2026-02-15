package com.example.projekatfaza23.model

import android.R
import android.util.Log
import androidx.compose.animation.core.snap
import com.example.projekatfaza23.data.db.LeaveDao
import com.example.projekatfaza23.data.db.LeaveRequestEntity
import com.example.projekatfaza23.data.db.UserEntity
import com.example.projekatfaza23.data.repository.toEntity
import com.example.projekatfaza23.data.repository.toLeaveRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestoreSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID


interface LeaveRepositoryI {
    fun getLeaveHistory(userEmail: String) : Flow<List<LeaveRequest>>
    suspend fun submitNewRequest(request: LeaveRequest, userEmail: String) : Boolean

    //za dekana
    fun getAllRequests() : Flow<List<LeaveRequest>>

    fun getAllEmployees(): Flow<List<UserEntity>>
    suspend fun updateReqeust(requestId: String, newStatus: RequestSatus, explanationText: String): Boolean
    suspend fun syncRequestsWithFirestore(userEmail: String)
    fun startRealtimeSync(userEmail: String): Flow<Unit>
    suspend fun updateEmployeeStatus(email: String, newStatus: String) : Boolean
}

class LeaveRepository(private val leaveDao: LeaveDao) : LeaveRepositoryI {
    private val firestore = FirebaseFirestore.getInstance()

    override fun getLeaveHistory(userEmail: String): Flow<List<LeaveRequest>> {
        return leaveDao.getRequestsForUser(userEmail).map { entities ->
            entities.map { it.toLeaveRequest() }
        }
    }

    override fun getAllRequests(): Flow<List<LeaveRequest>> = callbackFlow {
        val listener = firestore.collection("leave_request")
            //dodati sortiranje po datumu kreiranja , dodati u requeste
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DEBUG_DEAN", "CRITICAL ERROR: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val rawCount = snapshot.documents.size
                    Log.d("DEBUG_DEAN", "Firestore kaže da ima $rawCount dokumenata.")

                    if (rawCount == 0) {
                        Log.w(
                            "DEBUG_DEAN",
                            "Baza je vratila 0 dokumenata! Provjeri Rules ili ime kolekcije."
                        )
                    }

                    val isFromCache = snapshot.metadata.isFromCache

                    val remoteData = snapshot.documents.mapNotNull { doc ->
                        try {
                            val req = doc.toObject(LeaveRequest::class.java)
                            if (req == null) {
                                Log.e("DEBUG_DEAN", "Dokument ${doc.id} je NULL nakon konverzije.")
                            }
                            req?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e("DEBUG_DEAN", "GREŠKA MAPIRANJA ID ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    Log.d("DEBUG_DEAN", "Uspješno mapirano: ${remoteData.size} od $rawCount")
                    if (remoteData.isNotEmpty()) {
                        // localDao.insertAll(remoteData) // refresh lokalne baze
                        trySend(remoteData) // salje podatke sa servera
                        Log.d("DEBUG_DEAN", "Uspješno mapirano: ${remoteData.size} od $rawCount")

                    } else if (!isFromCache) {
                        Log.e("DEBUG_DEAN", "Snapshot je null!")
                        /*
                   if(localData.isEmpty()){
                    trySend(fakeRepo.getLeaveHistory())
                   }else{
                       trySend(localData)
                       }
                    */
                        trySend(emptyList())
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateReqeust(
        requestId: String,
        newStatus: RequestSatus,
        explanationText: String
    ): Boolean {
        return try {
            firestore.collection("leave_request")
                .document(requestId)
                .update("status", newStatus, "explanationDean", explanationText)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateRequestSecretary(
        requestId: String,
        newStatus: RequestSatus,
        explanationText: String,
        finalDates: List<LeaveDates>
    ): Boolean {
        return try {
            firestore.collection("leave_request")
                .document(requestId)
                .update(mapOf(
                    "status" to newStatus,
                    "explanationSecretary" to explanationText,
                    "leave_dates" to finalDates
                ))
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun submitNewRequest(request: LeaveRequest, userEmail: String): Boolean {
        //ovaj id je bio problem ako je offline (generisemo svoj id sad)
        val newId = request.id.ifEmpty { UUID.randomUUID().toString() }
        val currentTime = Timestamp.now()

        val finalRequest = request.copy(
            id = newId,
            userEmail = userEmail,
            createdAt = currentTime,
            status = RequestSatus.Pending
        )
        return try {
            leaveDao.insertRequests(listOf(finalRequest.toEntity()))
            firestore.collection("leave_request").document(newId).set(finalRequest)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun syncRequestsWithFirestore(userEmail: String) {
        try {
            val querySnapshot = firestore.collection("leave_request")
                .whereEqualTo("userEmail", userEmail)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val newEntities = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data
                        if (data == null) return@mapNotNull null

                        val request = doc.toObject(LeaveRequest::class.java)
                        request?.toEntity()
                    } catch (e: Exception) {
                        null
                    }
                }
                if (newEntities.isNotEmpty()) {
                    leaveDao.insertRequests(newEntities)
                }
            } else {
                Log.w("syncRequestsWithFirestore", "Qurey returned 0 results!")
            }
        } catch (e: Exception) {
            Log.e("syncRequestsWithFirestore", "Error in sync func: ${e.message}")
        }
    }

    override fun startRealtimeSync(userEmail: String): Flow<Unit> = callbackFlow {
        if (userEmail.isEmpty()) {
            close()
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection("leave_request")
            .whereEqualTo("userEmail", userEmail)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    launch {
                        val entities = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(LeaveRequest::class.java)?.copy(id = doc.id)?.toEntity()
                        }
                        leaveDao.insertRequests(entities)
                    }
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }


    override fun getAllEmployees(): Flow<List<UserEntity>> = callbackFlow {
        val listener = firestore.collection("user_info")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val employees = snapshot.documents.mapNotNull { doc ->
                        val email = doc.getString("email")
                        if (email != null) {
                            UserEntity(
                                email = email,
                                firstName = doc.getString("firstName") ?: "",
                                lastName = doc.getString("lastName") ?: "",
                                imageUrl = doc.getString("imageUrl"),
                                totalDays = doc.getLong("totalDays")?.toInt() ?: 20,
                                usedDays = doc.getLong("usedDays")?.toInt() ?: 0,
                                userStatus = doc.getString("employeeStatus") ?: "AtWork",
                                role = doc.getString("role") ?: "Professor"
                            )
                        } else {
                            null
                        }
                    }
                    trySend(employees)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun updateEmployeeStatus(email: String, newStatus: String): Boolean {
        return try {
            leaveDao.updateUserStatus(email, newStatus)
            val querySnapshot = firestore.collection("user_info")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                for (doc in querySnapshot.documents) {
                    firestore.collection("user_info")
                        .document(doc.id)
                        .update("employeeStatus", newStatus)
                        .await()
                }
            } else {
                Log.d("updateEmployeeStatus", "U bazi ne postoji korisnik sa emailom $email na Firestore")
            }
            true
        } catch (e: Exception) {
            false
        }
    }

}