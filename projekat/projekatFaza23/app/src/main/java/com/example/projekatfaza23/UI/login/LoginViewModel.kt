package com.example.projekatfaza23.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.data.auth.GoogleAuth
import com.example.projekatfaza23.data.auth.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class  LoginViewModel (
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    private val authService = GoogleAuth(application.applicationContext)

    fun loginWithGoogle(activityContext: Context, navigateHome: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val profile = authService.GoogleSignIn(activityContext)

                if (profile != null) {
                    UserManager.saveUser(profile)
                    if (profile != null) {
                        UserManager.saveUser(profile)
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        navigateHome()
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    navigateHome()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Sign in with Google failed. Try again!"
                    )
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
