package com.example.projekatfaza23.UI.dean

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.data.db.AppDatabase
import com.example.projekatfaza23.data.db.UserEntity
import com.example.projekatfaza23.data.repository.UserRepository
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
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Calendar


class DeanViewModel(application: Application): AndroidViewModel(application) {
    private val leaveDao = AppDatabase.getInstance(application).leaveDao()
    private val userRepository = UserRepository(leaveDao)

    private val _repository : LeaveRepositoryI = LeaveRepository(leaveDao)
    private val _uiState = MutableStateFlow(DeanUIState(isLoading = true))
    val uiState: StateFlow<DeanUIState> = _uiState.asStateFlow()


    init {
        loadAllRequests()
        loadEmployees()
    }

    private fun loadAllRequests() {
        viewModelScope.launch {
            _repository.getAllRequests()
                .catch { e ->
                    Log.e("DeanVM", "Error loading requests", e)
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { requests ->
                    val deanRequests = requests.filter{ it.status != RequestSatus.Pending }
                    _uiState.update { currentState ->
                        val onLeaveCount = calculateOnLeaveToday(requests)

                        val newState = currentState.copy(
                            requests = deanRequests,
                            isLoading = false,
                            onTodayLeaveCount = onLeaveCount)
                        applyFilters(newState)
                    }
                }
        }
    }

    fun updateEmployeeSearch(query: String) {
        _uiState.update { currentState ->
            val filteredEmp = if (query.isBlank()) currentState.employees
            else currentState.employees.filter {
                it.firstName.contains(query, ignoreCase = true) ||
                        it.lastName.contains(query, ignoreCase = true)
            }

            currentState.copy(
                employeeSearchQuery = query,
                displayedEmployees = filteredEmp
            )
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _repository.getAllEmployees()
                .catch { error ->
                    Log.e("DeanViewModel", "Error loading employees", error)
                }
                .collect { usersList ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            employees = usersList,
                            displayedEmployees = usersList
                        )
                    }
                }
        }
    }

    private fun applyFilters(state: DeanUIState): DeanUIState {
        val filtered = state.requests.filter { req ->
            if (req.status == RequestSatus.Pending) return@filter false  // ne  zelim da dekan vidi pending za sekretara, nepotrebno je

            val matchesStatus = if (state.filterStatus == "All") true
            else req.status.name.equals(state.filterStatus, ignoreCase = true)

            val matchesType =  if(state.currentRequestType.isEmpty()) true
            else {
                if (req.type.equals(state.currentRequestType)) true
                else false
            }

            val matchesName = if (state.searchQuery.isBlank()) true
            else {
                val query = state.searchQuery.trim()
                val queryParts = query.split("\\s+".toRegex())

                // trazimo u zaposlenim
                val matchingEmails = state.employees.filter { employee ->
                    val fullName = "${employee.firstName} ${employee.lastName}"

                    queryParts.all { part ->
                        fullName.contains(part, ignoreCase = true)
                    }
                }.map { it.email }.toSet()

                val isNameMatch = req.userEmail in matchingEmails

                val isEmailMatch = queryParts.all { part ->
                    req.userEmail.contains(part, ignoreCase = true)
                }
                isNameMatch || isEmailMatch
            }
            val (start, end) = state.dateRange
            val matchesDate = if (start == null || end == null) true
            else {
                val reqStart = req.leave_dates?.firstOrNull()?.start?.seconds?.times(1000) ?: 0L
                val reqEnd = req.leave_dates?.firstOrNull()?.end?.seconds?.times(1000) ?: 0L
                reqStart >= start && reqEnd <= end
            }

            matchesStatus && matchesName && matchesDate && matchesType
        }

        val isFilterActive = state.searchQuery.isNotBlank() ||
                state.dateRange.first != null || state.currentRequestType.isNotBlank()

        return state.copy(
            displayRequests = filtered,
            isActiveFilter = isFilterActive
        )
    }


    fun setExplanationDean(explanationText : String){
        _uiState.update { currentState ->
            val updatedRequest = currentState.selectedRequest?.copy(explanationDean = explanationText)
            currentState.copy(selectedRequest = updatedRequest)
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
    fun approveRequest(request: LeaveRequest){
        viewModelScope.launch {
            val requestEmail = request.userEmail
            var durationInDays = 0
            val startDate = request.leave_dates?.firstOrNull()?.start?.toDate()
            val endDate = request.leave_dates?.firstOrNull()?.end?.toDate()
            if(startDate != null && endDate != null) {
                val diffInMillis = endDate.time - startDate.time

                durationInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
            }

            if(durationInDays > 0) {
                _repository.updateReqeust(request.id, RequestSatus.Approved,request.explanationDean)

                val success = userRepository.updateLeaveDays(requestEmail, durationInDays)

                if (success) {
                    Log.d("DeanViewModel", "Uspješno smanjen broj dana korisniku.")
                } else {
                    Log.e("DeanViewModel", "Greška: dani nisu smanjeni.")
                }
            } else{
                Log.e("DeanViewModel", "Greška: Izračunati broj dana je 0 ili manji.")
            }
        }
        resetSelectedRequest()
    }

    fun denyRequest(request: LeaveRequest){
        viewModelScope.launch {
            _repository.updateReqeust(request.id, RequestSatus.Denied, request.explanationDean)

        }
        resetSelectedRequest()
    }


    fun onTypeChange(newType: String){
        _uiState.update { currentState ->
            val newState = currentState.copy(currentRequestType = newType)
            applyFilters(newState)
        }
    }

    fun resetSelectedRequest(){
        _uiState.update { it.copy(selectedRequest = null) }
    }

    fun setSelectedRequest(request: LeaveRequest){
        _uiState.update { it.copy(selectedRequest = request) }
    }

    //filtriranje funckije
    fun setStatusFilter(status: String){
        _uiState.update { currentState ->
            val newState = currentState.copy(filterStatus = status)
            applyFilters(newState)
        }    }


    fun updateNameFilter(name : String){
        _uiState.update { currentState ->
            val newState = currentState.copy(searchQuery = name)
            applyFilters(newState)
        }
    }

    fun updateDateRangeFilter(startMillis: Long?, endMillis: Long?){
        _uiState.update { currentState ->
            val newState = currentState.copy(dateRange = startMillis to endMillis)
            applyFilters(newState)
        }
    }

    fun resetFilters(){
        _uiState.update { currentState ->
            val newState = currentState.copy(
                filterStatus = "All",
                dateRange = null to null,
                searchQuery = "",
                currentRequestType = ""
            )
            applyFilters(newState)
        }
    }

    fun resetEmployeeSearchQuery(){
        _uiState.update { currentState ->
          currentState.copy(
                employeeSearchQuery =  "",
                displayedEmployees = currentState.employees
            )
        }
    }


}