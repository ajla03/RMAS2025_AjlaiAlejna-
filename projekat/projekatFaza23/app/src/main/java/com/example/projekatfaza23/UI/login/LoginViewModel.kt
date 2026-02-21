package com.example.projekatfaza23.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.UI.navigation.Screen
import com.example.projekatfaza23.data.auth.GoogleAuth
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.data.db.AppDatabase
import com.example.projekatfaza23.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.projekatfaza23.UI.Role


class  LoginViewModel (
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()
    private val database = AppDatabase.getInstance(application)
    private val userRepository = UserRepository(database.leaveDao())
    private val leaveRepository = LeaveRepository(database.leaveDao())

    private val authService = GoogleAuth(application.applicationContext)

    fun loginWithGoogle(activityContext: Context, navigateHome: (Screen) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val profile = authService.GoogleSignIn(activityContext)

                if (profile != null) {
                    val userRole = userRepository.syncUserAfterLogin(profile)

                    if(userRole != null){
                        UserManager.saveUser(profile)
                        
                        val destination = when (userRole) {
                            Role.Dean.name -> Screen.DeanHome
                            Role.Secretary.name -> Screen.SecretaryHomeScreen
                            else -> Screen.Home
                        }
                        
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        navigateHome(destination)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Syncing user data with database data failed"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error occured: ${e.message ?: "Unknown mistake"}"
                )
            }
        }
    }
}
