package com.example.projekatfaza23.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserManager {
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun saveUser(profile: UserProfile){
        _currentUser.value = profile
    }

    fun logOutUser(){
        _currentUser.value = null
    }
}