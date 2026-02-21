package com.example.projekatfaza23.data.auth

import android.net.Uri
import com.example.projekatfaza23.UI.Role

data class UserProfile(
    //TODO implementirati id generisanje za usera
    val name: String,
    val lastName: String,
    val email: String,
    val profilePictureURL: Uri?,
    val phoneNumber: String?,
    //idToken je JWT
    val idToken: String,
    var role: String? = "Professor"
)
