package com.example.projekatfaza23.UI.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.model.FakeLeaveRepository
import com.example.projekatfaza23.model.LeaveRepository
import com.example.projekatfaza23.model.LeaveRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




class InboxRequestViewModel(private val repository : LeaveRepository = FakeLeaveRepository()): ViewModel() {
    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState: StateFlow<LeaveUiState> = _uiState.asStateFlow()


    private val _currentFilter = MutableStateFlow("All")
    val currentFilter : StateFlow<String> = _currentFilter.asStateFlow()

    fun setFilter(filter: String) {
        _currentFilter.value = filter
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val data = repository.getLeaveHistory()
            _uiState.update { it.copy(
                requestHistory = data,
                isLoading = false
            ) }
        }
    }

    fun submitLeaveRequest(){
        // logika za slannje na server requesta
        val requestToSend = _uiState.value.currentRequest
        _uiState.update { currentState ->
            currentState.copy(
                requestHistory = currentState.requestHistory + requestToSend,
                currentRequest = LeaveRequest(
                    type = "",
                    explanation = "",
                    fileName = "",
                    dateFrom = "",
                    dateTo = ""
                )
            )
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

    fun onDatesSelected(from: String, to: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentRequest = currentState.currentRequest.copy(dateFrom = from, dateTo = to)
            )
        }
    }

    fun onFileAttached(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentRequest = currentState.currentRequest.copy(fileName = name)
            )
        }
    }
}

