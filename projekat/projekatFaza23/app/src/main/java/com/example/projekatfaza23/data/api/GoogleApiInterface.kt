package com.example.projekatfaza23.data.api

import retrofit2.http.GET

interface GooglePeopleApi {
    @GET("v1/people/me?personFields=photos")
    suspend fun getUserProfilePhoto() : GoogleApiUserResponse
}