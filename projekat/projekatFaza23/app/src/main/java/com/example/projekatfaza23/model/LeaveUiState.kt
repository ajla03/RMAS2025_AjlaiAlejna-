package com.example.projekatfaza23.model


enum class Status{
    AtWork,
    PaidLeave,
    AnnualLeave
}

data class LeaveUiState(
    val totalDays: Int = 0,
    val usedDays : Int = 0,
    val status : Status = Status.AtWork,
    val remainingLeaveDays : Int = totalDays - usedDays,
    val requestHistory: List<LeaveRequest> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMsg : String? = null
)
