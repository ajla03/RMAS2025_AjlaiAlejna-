package com.example.projekatfaza23.UI.home

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.model.FakeLeaveRepository
import com.example.projekatfaza23.model.LeaveRepository
import com.example.projekatfaza23.model.LeaveRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




class InboxRequestViewModel(): ViewModel() {
    private val _repository = MutableStateFlow<LeaveRepository?>(null)

    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState: StateFlow<LeaveUiState> = _uiState.asStateFlow()
    private val _currentFilter = MutableStateFlow("All")
    val currentFilter : StateFlow<String> = _currentFilter.asStateFlow()

    init {
        viewModelScope.launch {
            UserManager.currentUser.collect{ user ->
                if (user != null && user.email != null){
                    _repository.value = LeaveRepository(userEmail = user.email)
                    loadUserLeaveData()
                }
            }
        }
    }


    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun sendRequest(){
        val repo = _repository.value
        if (repo == null) return

        val requestToSend = _uiState.value.currentRequest

        viewModelScope.launch {
            if(requestToSend.type.isEmpty() || requestToSend.dateFrom==null || requestToSend.dateTo==null) {
                _uiState.update {
                    it.copy(
                        isSuccess = false,
                        isError = true,
                        errorMsg = "Date and Type fields are mandatory!"
                    )
                }
                 delay(3000)
                _uiState.update { it.copy(isError = false, errorMsg = null) }
                return@launch
            }else {
                _uiState.update { it.copy(isLoading = true, isSuccess = false, isError = false) }

                val success = repo.submitNewRequest(requestToSend)

                if (success) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isSuccess = true,
                            currentRequest = LeaveRequest()
                        )

                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMsg = "Failed to send request"
                        )
                    }
                }
            }
        }
    }
    fun getFilteredRequests():List<LeaveRequest> {
        val allRequests = _uiState.value.requestHistory
        val filter = _currentFilter.value
        return if (filter == "All"){
            allRequests
        }else{
            allRequests.filter{ it.status.name == filter }
        }
    }

    init {
        loadUserLeaveData()
    }


    private fun loadUserLeaveData(){
        val repo = _repository.value
        if (repo == null) return

        Log.d("test#################################3", "${UserManager.currentUser.value?.email}")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.getLeaveHistory()
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }.collect { novaLista ->
                _uiState.update {
                    currentState ->
                    currentState.copy(
                        requestHistory = novaLista
                            .sortedByDescending { it.dateFrom },
                        isLoading = false
                    )
                }
            }
        }
    }


    private fun calculateWorkingDays(){}

    fun clearError(){}

    fun onTypeChange(newType: String){
        _uiState.update { currentState ->
            currentState.copy(
                currentRequest = currentState.currentRequest.copy(type = newType)
            )
        }
    }

    fun onExplanationChange(newExplanation: String){
        _uiState.update{ currentState ->
            currentState.copy(
                currentRequest = currentState.currentRequest.copy( explanation = newExplanation)
            )
        }
    }

    fun onDatesSelected(from: Long?, to: Long?) {
        _uiState.update { it.copy(
                currentRequest = it.currentRequest.copy(
                    dateFrom = from ?: 0L,
                    dateTo = to ?: 0L
                )
          )
        }
    }

    fun onFileAttached(uri: android.net.Uri?, name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentRequest = currentState.currentRequest.copy(fileName = name,
                    fileUri = uri?.toString())
            )
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}

