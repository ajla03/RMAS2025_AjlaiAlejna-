package com.example.projekatfaza23.UI.request

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatfaza23.UI.home.LeaveUiState
import com.example.projekatfaza23.UI.home.Status
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.data.db.AppDatabase
import com.example.projekatfaza23.data.db.LeaveDao
import com.example.projekatfaza23.data.repository.GoogleProfileRepository
import com.example.projekatfaza23.data.repository.UserRepository
import com.example.projekatfaza23.model.FileInfo
import com.example.projekatfaza23.model.LeaveDates
import com.example.projekatfaza23.model.LeaveRepository
import com.example.projekatfaza23.model.LeaveRepositoryI
import com.example.projekatfaza23.model.LeaveRequest
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InboxRequestViewModel(application: Application): AndroidViewModel(application) {
    private val leaveDao = AppDatabase.getInstance(application).leaveDao()
    private val _repository : LeaveRepositoryI = LeaveRepository(leaveDao)
    private val userRepo = UserRepository(leaveDao)

    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState: StateFlow<LeaveUiState> = _uiState.asStateFlow()
    private val _currentFilter = MutableStateFlow("All")
    val currentFilter : StateFlow<String> = _currentFilter.asStateFlow()
    private var currentUserEmail: String? = null

    init {
        viewModelScope.launch {
            UserManager.currentUser.collect{ user ->
                if (user != null && user.email != null){
                    currentUserEmail = user.email

                    launch {
                        userRepo.realTimeUserSync(user.email).collect()
                    }

                    loadUserLeaveData(user.email)
                }
            }
        }
        monitorUserData()
    }


    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun sendRequest(){

        val email = currentUserEmail
        if(email == null){
            _uiState.update { it.copy(isError = true, errorMsg = "User not logged in!") }
            return
        }
        val requestToSend = _uiState.value.currentRequest

        viewModelScope.launch {
            if(requestToSend.type.isEmpty() || requestToSend.leave_dates?.firstOrNull()?.start == null || requestToSend.leave_dates.firstOrNull()?.end ==null) {
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

                val success = _repository.submitNewRequest(requestToSend, email)

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



    private fun loadUserLeaveData(email: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            launch {
                    //_repository.syncRequestsWithFirestore(email)
                _repository.startRealtimeSync(email)
                    .catch { e -> Log.e("RealTimeSync", "Sync error: ${e.message}") }
                    .collect()
            }
            _repository.getLeaveHistory(email)
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }.collect { novaLista ->
                _uiState.update {
                    currentState ->
                    currentState.copy(
                        requestHistory = novaLista.sortedByDescending { it.createdAt },
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
        if(from == null || to == null ) return

        _uiState.update { currentState ->
            val newDateRange = LeaveDates(start = Timestamp(java.util.Date(from)),
                                          end = Timestamp(java.util.Date(to)))

            val oldList = currentState.currentRequest.leave_dates  ?: emptyList<LeaveDates>()
            val updatedList = oldList + newDateRange

            currentState.copy(
                currentRequest = currentState.currentRequest.copy(
                    leave_dates = updatedList
                )
          )
        }
    }

    fun removeDateRange(index: Int){
        _uiState.update { currentState ->
            val updatedList = (currentState.currentRequest.leave_dates ?: emptyList<LeaveDates>())
                .filterIndexed { i, _ -> i!=index }

            currentState.copy(
                currentRequest = currentState.currentRequest.copy(leave_dates = updatedList)
            )
        }
    }

    fun onFileAttached(uri: Uri?, name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentRequest = currentState.currentRequest.copy(
                    file_info = FileInfo(
                        file_name = name,
                        file_type = "",
                        uri = uri.toString()
                    )
                )
            )
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun updateStatus(newStatus: Status){
        val email = currentUserEmail

        if (email == null){
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMsg = "User not signed in"
                )
                return
            }
        }

        viewModelScope.launch {
            _uiState.update {
                    currentState ->
                currentState.copy(status = newStatus)
            }

            if(!_repository.updateEmployeeStatus(email ?: "", newStatus.name)){
                _uiState.update {
                    it.copy(
                        isError = true,
                        errorMsg = "Unexpected error"
                    )
                }
            }
        }
    }

    private fun monitorUserData() {
        viewModelScope.launch {
            UserManager.currentUser.collect { userProfile ->
                val email = userProfile?.email

                if (email != null) {
                    userRepo.getUser(email).collect { userEntity ->
                        userEntity?.let { user ->

                            _uiState.update { currState ->
                                currState.copy(
                                    totalDays = user.totalDays,
                                    usedDays = user.usedDays,
                                    remainingLeaveDays = user.totalDays - user.usedDays
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}