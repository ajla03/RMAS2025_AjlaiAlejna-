package com.example.projekatfaza23.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePeopleApi {
    @GET("v1/people/me")
    suspend fun getUserProfilePhoto(
        @Query("personFields") personFields:String = "photos"
    ) : GoogleApiUserResponse
}