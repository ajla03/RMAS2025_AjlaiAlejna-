package com.example.projekatfaza23.data.repository

import android.util.Log
import com.example.projekatfaza23.data.api.RetrofitInstance

class GoogleProfileRepository {
    suspend fun getProfilePictureUrl() : String? {
        return try {
            val response = RetrofitInstance.googlePeopleApi.getUserProfilePhoto()
            response.photos?.firstOrNull()?.url
        } catch (e: Exception) {
            Log.e("Google People Api", "Obtaining profile photo failed: ${e.message}")
            null
        }
    }
}