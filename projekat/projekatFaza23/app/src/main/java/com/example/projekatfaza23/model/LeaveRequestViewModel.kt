package com.example.projekatfaza23.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




class LeaveRequestViewModel(private val repository : LeaveRepository = FakeLeaveRepository()): ViewModel() {
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
            allRequests.filter{ it.status == filter }
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

    fun submitLeaveRequest(){}

    private fun calculateWorkingDays(){}

    fun clearError(){}

}

