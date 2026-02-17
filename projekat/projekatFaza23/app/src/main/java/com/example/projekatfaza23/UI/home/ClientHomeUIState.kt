package com.example.projekatfaza23.UI.home

import com.example.projekatfaza23.model.LeaveRequest


enum class Status (val statusString: String){
    AtWork("Na poslu"),
    PaidLeave("Plaćeno odsustvo"),
    AnnualLeave("Godišnji odmor"),
    Away("Odsutan/na")
}

data class LeaveUiState(
    val totalDays: Int = 0,
    val usedDays : Int = 0,
    val status : Status = Status.AtWork,
    val remainingLeaveDays : Int = totalDays - usedDays,
    val requestHistory: List<LeaveRequest> = emptyList(),
    val currentRequest: LeaveRequest = LeaveRequest(),
    val showWarningShortNotice: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess : Boolean = false,
    val isError: Boolean = false,
    val errorMsg : String? = null
)
