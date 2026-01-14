package com.example.projekatfaza23.model

import android.util.Log
import androidx.compose.animation.core.snap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestoreSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


interface LeaveRepositoryI {
    fun getLeaveHistory() : Flow<List<LeaveRequest>>
    suspend fun submitNewRequest(request: LeaveRequest) : Boolean
}

class LeaveRepository(
    //private val leaveDao : LeaveDao,
    private val userEmail : String
) : LeaveRepositoryI {
    private val firestore = FirebaseFirestore.getInstance()
init {
    val settings = firestoreSettings {
        setLocalCacheSettings(
            PersistentCacheSettings.newBuilder()
                .setSizeBytes(100 * 1024 * 1024)
                .build()
        )
    }
    firestore.firestoreSettings = settings
}
    private val fakeRepo = FakeLeaveRepository()
    override fun getLeaveHistory(): Flow<List<LeaveRequest>> =
        callbackFlow {
            // val localData = localDao.getAllRequests(userEmail)
            val listener = firestore.collection("leave_request")
                .whereEqualTo("userEmail", userEmail)
                .addSnapshotListener { snapshot, error ->
                    //ako se desi greska, npr nema interneta
                    if(error != null){
                        /*
                        //ili da koristimo Firebase Offline podrsku i da nemamo lokalne baze ??
                        if(localData.isEmpty()){
                         trySend(fakeRepo.getLeaveHistory())
                        }else{
                            trySend(localData)
                            }
                         */
//                        trySend(fakeRepo.getLeaveHistorySync())
                        return@addSnapshotListener // Dodano da ne ide dalje u slučaju greške
                    }

                    if(snapshot != null){
                        val isFromCache = snapshot.metadata.isFromCache

                        val remoteData = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(LeaveRequest::class.java)?.copy(
                                // Firebase ID dokumenta kao unikatni ključ
                                id = doc.id
                            )
                        }

                        if (remoteData.isNotEmpty()) {
                            // localDao.insertAll(remoteData) // refresh lokalne baze
                            trySend(remoteData) // salje podatke sa servera
                        } else if (!isFromCache){
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


    override suspend fun submitNewRequest(request: LeaveRequest): Boolean {
       val finalRequest = request.copy( userEmail = userEmail)
       return  try{
           val result = firestore.collection("leave_request").add(finalRequest).await()
           true
       }catch (e : Exception){
           return false
       }
    }
}