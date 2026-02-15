package com.example.projekatfaza23.UI.secretary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.data.db.AppDatabase
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
                            state.copy(
                                allRequests = requests,
                                displayedRequests = pendingOnly,
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

    fun validateRequest() {
        val state = _uiState.value
        val req = state.selectedRequest ?: return

        viewModelScope.launch {
            repository.updateReqeust(req.id, RequestSatus.PendingDean, state.explanationSecretary)
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
            calculateDurationInDays(dateRange?.start, dateRange?.end)
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

    private fun calculateDurationInDays(start: Timestamp?, end: Timestamp?): Int {
        if (start == null || end == null) return 0

        val startMillis = start.toDate().time
        val endMillis = end.toDate().time

        val diff = endMillis - startMillis
        val days = (diff / (1000 * 60 * 60 * 24)).toInt() + 1

        return if (days > 0) days else 0
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
}