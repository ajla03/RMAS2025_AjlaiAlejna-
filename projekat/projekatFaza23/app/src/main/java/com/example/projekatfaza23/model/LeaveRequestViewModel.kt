package com.example.projekatfaza23.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



enum class Status{
    WorkInOffice,
    WorkRemote,
    SickLeave,
    Vacay
}


data class LeaveUiState(
    val totalDays: Int = 0,
    val usedDays : Int = 0,
    val pendingDays : Int = 0,
    val status : Status = Status.WorkInOffice,
    val remainingDays : Int = 0,
    val requestHistory: List<LeaveRequest> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg : String? = null
)

class LeaveRequest(
    val id: Int = 0,
    val status : String = "",
    val type: String = "",
    val dateFrom : String = "",
    val dateTo : String = ""
){

}

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

