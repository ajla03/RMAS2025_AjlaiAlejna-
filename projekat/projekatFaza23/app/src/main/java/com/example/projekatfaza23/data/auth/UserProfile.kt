package com.example.projekatfaza23.data.auth

import android.net.Uri

data class UserProfile(
    //TODO implementirati id generisanje za usera
    val name: String?,
    val lastName: String?,
    val email: String?,
    val profilePictureURL: Uri?,
    val phoneNumber: String?,
    //idToken je oauthToken koji se koristi za slanje req na Google API endpoint
    val idToken: String
)
