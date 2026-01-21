package com.example.projekatfaza23.data.repository

import com.example.projekatfaza23.data.auth.UserProfile
import com.example.projekatfaza23.data.db.UserEntity

fun mapToEntity(googleProfile: UserProfile,
                role: String,
                status: String,
                total: Int,
                used: Int) : UserEntity {
    return UserEntity(
        email = googleProfile.email ?: "",
        firstName = googleProfile.name ?: "",
        lastName = googleProfile.lastName ?: "",
        imageUrl = googleProfile.profilePictureURL.toString(),
        role = role,
        totalDays = total,
        usedDays = used,
        userStatus = status
    )
}