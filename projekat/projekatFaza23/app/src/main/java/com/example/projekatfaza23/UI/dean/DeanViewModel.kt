package com.example.projekatfaza23.UI.dean

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.data.db.AppDatabase
import com.example.projekatfaza23.data.db.UserEntity
import com.example.projekatfaza23.model.LeaveRepository
import com.example.projekatfaza23.model.LeaveRepositoryI
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.ignoreIoExceptions
import kotlinx.coroutines.flow.combine
data class DeanUIState(
    val requests: List<LeaveRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isActiveFilter : Boolean = false
)

class DeanViewModel(application: Application): AndroidViewModel(application) {
    private val leaveDao = AppDatabase.getInstance(application).leaveDao()
    private val _repository : LeaveRepositoryI = LeaveRepository(leaveDao)
    private val _uiState = MutableStateFlow(DeanUIState(isLoading = true))
    val uiState: StateFlow<DeanUIState> = _uiState.asStateFlow()

    private val _filterStatus = MutableStateFlow("All")
    val filterStatus : StateFlow<String> = _filterStatus.asStateFlow()
    private val _searchName = MutableStateFlow("")
    val searchName: StateFlow<String> = _searchName.asStateFlow()
    private val _filterDateRange = MutableStateFlow<Pair<Long?, Long?>>(null to null)
    val filterDateRange: StateFlow<Pair<Long?, Long?>> = _filterDateRange.asStateFlow()

    private val _selectedRequest = MutableStateFlow<LeaveRequest?>(null)
    val selectedRequest : StateFlow<LeaveRequest?> = _selectedRequest.asStateFlow()

    private val _employees = MutableStateFlow<List<UserEntity>>(emptyList())
    private val _employeeSearchQuery = MutableStateFlow("")
    val employeeSearchQuery = _employeeSearchQuery.asStateFlow()


    val filteredRequests: StateFlow<List<LeaveRequest>> = combine(
        _uiState,
        _filterStatus,
        _searchName,
        _filterDateRange
    ) { state, status, name, dateRange ->

        state.requests.filter { request ->
            // 1. Status Filter
            val matchStatus = if (status == "All") true else request.status.name.equals(status, ignoreCase = true)
            val matchName = if (name.isBlank()) {true}
            else {
                val fullName = name.trim().split("\\s+".toRegex()) //hvata i brise prazna mjesta
                fullName.all {
                    term -> request.userEmail.contains(term, ignoreCase = true)
                }
            }

            val (filterStart, filterEnd) = dateRange
            val matchDate = if (filterStart == null || filterEnd == null) {
                true // ako nije odabran datum prikazat ce sve
            } else {
                val requestRange = request.leave_dates?.firstOrNull()
                val reqStart = requestRange?.start?.seconds?.times(1000)
                val reqEnd = requestRange?.end?.seconds?.times(1000)

                if (reqStart != null && reqEnd != null) {
                    (reqStart >= filterStart) && (reqEnd <= filterEnd)
                } else {
                    false
                }
            }
            matchStatus && matchName && matchDate
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredEmployees: StateFlow<List<UserEntity>> = combine(
        _employees,
        _employeeSearchQuery
    ) { employees, query ->
        if (query.isBlank()) {
            employees
        } else {
            employees.filter { user ->
                val fullName = "${user.firstName} ${user.lastName}"
                fullName.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadAllRequests()
        loadEmployees()
    }

    private fun loadAllRequests(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _repository.getAllRequests()
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }.collect { novaLista ->
                    Log.d("nova lista", "${novaLista.size}")
                    _uiState.update {
                            currentState ->
                        currentState.copy(
                            requests = novaLista
                                .sortedBy { it.leave_dates?.firstOrNull()?.start?.seconds ?: 0L },
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateEmployeeSearch(query: String) {
        _employeeSearchQuery.value = query
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _repository.getAllEmployees()
                .catch { error ->
                    Log.e("DeanViewModel", "Error loading employees", error)
                }
                .collect { usersList ->
                    _employees.value = usersList
                }
        }
    }

    fun setExplanationDean(explanationText : String){
        _selectedRequest.update{
            currentRequest ->
            currentRequest?.copy(explanationDean = explanationText)
        }
    }
    fun approveRequest(request: LeaveRequest){
        viewModelScope.launch {
            _repository.updateReqeust(request.id, RequestSatus.Approved,request.explanationDean)
        }
        resetSelectedRequest()
    }

    fun denyRequest(request: LeaveRequest){
        viewModelScope.launch {
            _repository.updateReqeust(request.id, RequestSatus.Denied, request.explanationDean)
        }
        resetSelectedRequest()
    }

    fun resetSelectedRequest(){
        _selectedRequest.value = null
    }

    fun setSelectedRequest(request: LeaveRequest){
        _selectedRequest.value = request
    }

    //filtriranje funckije
    fun setStatusFilter(status: String){
        _filterStatus.value = status
    }


    fun updateNameFilter(name : String){
        _searchName.value = name
    }

    fun updateDateRangeFilter(startMillis: Long?, endMillis: Long?){
        _filterDateRange.value = startMillis to endMillis
    }

    fun resetFilters(){
        _filterDateRange.value = null to null
        _searchName.value = ""
        _uiState.update { it.copy( isActiveFilter = false) }
    }

    fun checkActiveFilter(){
        val hasName = _searchName.value.isNotBlank()
        val hasDate = _filterDateRange.value.first!=null
        _uiState.update { it.copy(isActiveFilter = hasDate || hasName) }
    }

}