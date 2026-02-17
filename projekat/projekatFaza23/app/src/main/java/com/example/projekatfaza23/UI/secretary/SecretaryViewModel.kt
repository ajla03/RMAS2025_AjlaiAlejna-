package com.example.projekatfaza23.UI.secretary

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.UI.request.validationHelpers
import com.example.projekatfaza23.data.db.AppDatabase
import com.example.projekatfaza23.model.LeaveDates
import com.example.projekatfaza23.model.LeaveRepository
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar


class SecretaryViewModel(application: Application) : AndroidViewModel(application) {
    private val leaveDao = AppDatabase.getInstance(application).leaveDao()
    private val repository = LeaveRepository(leaveDao)

    private val _uiState = MutableStateFlow(SecretaryUIState(isLoading = true))
    val uiState : StateFlow<SecretaryUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData(){
        viewModelScope.launch {
            launch {
                repository.getAllEmployees().collect { employees ->
                    _uiState.update { it.copy(employees = employees) }
                    refreshSelectedEmployee()
                }
            }
            launch {
                repository.getAllRequests()
                    .catch { e -> _uiState.update { it.copy(error = e.message) } }
                    .collect { requests ->
                        _uiState.update { state ->
                            val pendingOnly = requests.filter { it.status == RequestSatus.Pending }
                            val onLeaveCount = calculateOnLeaveToday(requests)
                            state.copy(
                                allRequests = requests,
                                displayedRequests = pendingOnly,
                                onTodayLeaveCount = onLeaveCount,
                                isLoading = false
                            )
                        }
                    }
            }
        }
    }

    fun selectRequest(request: LeaveRequest){
        _uiState.update { it.copy(
            selectedRequest = request,
            explanationSecretary = request.explanationSecretary
        ) }
        refreshSelectedEmployee()
        calculateStatsForSelectedUser()
    }

    fun updateExplanation(text: String) {
        _uiState.update { it.copy(explanationSecretary = text) }
    }

    fun validateRequest(selectedDateRange: LeaveDates) {
        val state = _uiState.value
        val req = state.selectedRequest ?: return

        viewModelScope.launch {
            val purifiedList = listOf(selectedDateRange)

            repository.updateRequestSecretary(req.id,
                                              RequestSatus.PendingDean,
                                              state.explanationSecretary,
                                              purifiedList)

            _uiState.update { it.copy(selectedRequest = null) }
        }
    }

    fun denyRequest() {
        val state = _uiState.value
        val req = state.selectedRequest ?: return

        viewModelScope.launch {
            repository.updateReqeust(req.id, RequestSatus.Denied, state.explanationSecretary)
            _uiState.update { it.copy(selectedRequest = null) }
        }
    }

    private fun calculateStatsForSelectedUser() {
        val state = _uiState.value
        val currentRequest = state.selectedRequest ?: return
        val user = state.requestAuthor ?: return

        val totalDays = user.totalDays
        val usedDays = user.usedDays

        val pendingDays = currentRequest.leave_dates?.sumOf { dateRange ->
            if (dateRange?.start != null && dateRange.end != null) {
                validationHelpers.countWorkDays(dateRange.start, dateRange.end)
            } else {
                0
            }
        } ?: 0

        val remainingDays = totalDays - usedDays

        _uiState.update {
            it.copy(stats = UserVacationStats(
                totalDays = totalDays,
                usedDays = usedDays,
                pendingDays = pendingDays,
                remainingDays = remainingDays
            ))
        }
    }

    private fun calculateOnLeaveToday(requests: List<LeaveRequest>): Int{
        val now  = System.currentTimeMillis()

        val activeRequests = requests.filter { request ->
            request.status == RequestSatus.Approved &&
            request.leave_dates?.any{ dateRange ->
                val startMillis = dateRange?.start?.toDate()?.time ?: Long.MAX_VALUE
                val endMillis = dateRange?.end?.toDate()?.time ?: Long.MIN_VALUE

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = endMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val adjustedEndMillis = calendar.timeInMillis

                now in startMillis..adjustedEndMillis
            } == true
        }
        return activeRequests.map { it.userEmail }.distinct().size
    }

    private fun refreshSelectedEmployee() {
        _uiState.update { state ->
            val author = state.employees.find { it.email == state.selectedRequest?.userEmail }
            state.copy(requestAuthor = author)
        }
    }

    fun updateHistorySearchQuery(query: String) {
        _uiState.update { it.copy(historySearchQuery = query) }
    }

    fun updateHistoryFilter(filter: HistoryFilter) {
        _uiState.update { it.copy(historyFilter = filter) }
    }

    fun resetHistoryFilters() {
        _uiState.update { currentState ->
            currentState.copy(
                historySearchQuery = "",
                historyFilter = HistoryFilter.ALL
            )
        }
    }
}