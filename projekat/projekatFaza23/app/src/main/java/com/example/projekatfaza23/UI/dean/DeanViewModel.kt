package com.example.projekatfaza23.UI.dean

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.data.auth.UserManager
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

class DeanViewModel(): ViewModel() {
    private val _repository : LeaveRepositoryI = LeaveRepository()
    private val _uiState = MutableStateFlow(DeanUIState(isLoading = true))
    val uiState: StateFlow<DeanUIState> = _uiState.asStateFlow()

    //za filtriranje
    private val _filterStatus = MutableStateFlow("All")
    private val _searchName = MutableStateFlow("")
    private val _searchDate = MutableStateFlow("")


    val filteredRequests : StateFlow<List<LeaveRequest>> = combine(
        _uiState,
        _filterStatus,
        _searchName,
        _searchDate
    ){ state: DeanUIState, status: String, name: String, date: String ->

        val originalList = state.requests
        originalList.filter { request ->
            val matchStatus = if (status == "All") true else request.status.name.equals(status, ignoreCase = true)
            val matchName = if (name.isBlank()) true else request.userEmail.contains(name, ignoreCase = true)

            matchStatus && matchName
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    init {
        loadAllRequests()
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

    fun approveRequest(request: LeaveRequest){
        viewModelScope.launch {
            _repository.updateReqeustStatus(request.id, RequestSatus.Approved)
        }
    }

    fun denyRequest(request: LeaveRequest){
        viewModelScope.launch {
            _repository.updateReqeustStatus(request.id, RequestSatus.Denied)
        }
    }

    //filtriranje funckije
    fun setStatusFilter(status: String){
        _filterStatus.value = status
    }

    fun setAdvancedFilter(date: String, name: String){
        _searchDate.value = date
        _searchName.value = name
        _uiState.update { it.copy( isActiveFilter = true) }
    }

    fun resetFilters(){
        _searchDate.value = ""
        _searchName.value = ""
        _uiState.update { it.copy( isActiveFilter = false) }

    }


}