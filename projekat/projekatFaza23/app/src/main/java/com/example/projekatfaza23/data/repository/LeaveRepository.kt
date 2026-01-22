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
    fun getLeaveHistory(userEmail: String) : Flow<List<LeaveRequest>>
    suspend fun submitNewRequest(request: LeaveRequest, userEmail: String) : Boolean

    //za dekana
    fun getAllRequests() : Flow<List<LeaveRequest>>
    suspend fun updateReqeustStatus(requestId: String, newStatus: RequestSatus): Boolean
}

class LeaveRepository() : LeaveRepositoryI {
    private val firestore = FirebaseFirestore.getInstance()

    override fun getLeaveHistory(userEmail: String): Flow<List<LeaveRequest>> =
        callbackFlow {
            val listener = firestore.collection("leave_request")
                .whereEqualTo("userEmail", userEmail)
                .addSnapshotListener { snapshot, error ->
                    //ako se desi greska, npr nema interneta
                    if(error != null){
                        close(error)
                        /*
                        if(localData.isEmpty()){
                         trySend(fakeRepo.getLeaveHistory())
                        }else{
                            trySend(localData)
                            }
                         */
                        return@addSnapshotListener // Dodano da ne ide dalje u slučaju greške
                    }

                    if(snapshot != null){
                        val remoteData = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(LeaveRequest::class.java)?.copy(
                                // Firebase ID dokumenta kao unikatni ključ
                                id = doc.id
                            )
                        }

                        trySend(remoteData)
                    }
                }
            awaitClose { listener.remove() }
        }


    override fun getAllRequests(): Flow<List<LeaveRequest>> = callbackFlow{
        val listener = firestore.collection("leave_request")
            //dodati sortiranje po datumu kreiranja , dodati u requeste
            .addSnapshotListener { snapshot, error ->
             if(error!=null) {
                 Log.e("DEBUG_DEAN", "CRITICAL ERROR: ${error.message}")
                 return@addSnapshotListener
             }
                if(snapshot != null){
                    val rawCount = snapshot.documents.size
                    Log.d("DEBUG_DEAN", "Firestore kaže da ima $rawCount dokumenata.")

                    if (rawCount == 0) {
                        Log.w("DEBUG_DEAN", "Baza je vratila 0 dokumenata! Provjeri Rules ili ime kolekcije.")
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

                    } else if (!isFromCache){
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

    override suspend fun updateReqeustStatus(requestId: String, newStatus: RequestSatus): Boolean {
        return try{
            firestore.collection("leave_request")
                .document(requestId)
                .update("status", newStatus)
                .await()
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
    override suspend fun submitNewRequest(request: LeaveRequest, userEmail: String): Boolean {
       val finalRequest = request.copy( userEmail = userEmail)
       return  try{
           val result = firestore.collection("leave_request").add(finalRequest).await()
           true
       }catch (e : Exception){
           return false
       }
    }
}