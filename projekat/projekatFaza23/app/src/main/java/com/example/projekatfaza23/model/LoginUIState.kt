package com.example.projekatfaza23.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUIState(
    val email : String = "",
    val password : String = "",
    val isLoginSuccessful : Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String?= null

)
