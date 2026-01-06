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
private val mockUsers = mapOf(
    "admin@test.com" to "admin123",
    "korisnik@hr.ba" to "lozinka123"
)
class  LoginViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(email : String){
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String){
        _uiState.update { it.copy(password = password) }
    }

    fun login(){
        val currentEmail = _uiState.value.email
        val currentPassword  = _uiState.value.password

        _uiState.update { it.copy(isLoading = true) }

        if(mockUsers[currentEmail]==currentPassword){
            _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
        }else{
            _uiState.update {
                it.copy(isLoginSuccessful = false,
                        isLoading = false,
                        errorMessage = "Pogresan email ili lozinka!")
            }
        }
    }
}
