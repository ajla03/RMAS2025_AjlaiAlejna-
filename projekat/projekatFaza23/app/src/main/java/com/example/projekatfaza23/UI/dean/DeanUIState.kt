package com.example.projekatfaza23.UI.dean

import com.example.projekatfaza23.data.db.UserEntity
import com.example.projekatfaza23.model.LeaveRequest

data class DeanUIState(
    val requests: List<LeaveRequest> = emptyList(),
    val displayRequests: List<LeaveRequest> = emptyList(),

    val employees: List<UserEntity> = emptyList(),
    val displayedEmployees: List<UserEntity> = emptyList(),
    val onTodayLeaveCount: Int = 0,

    val isLoading: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val filterStatus: String = "All",
    val dateRange: Pair<Long?, Long?> = null to null,
    val isActiveFilter: Boolean = false,

    val employeeSearchQuery: String = "",

    val selectedRequest: LeaveRequest? = null,
    val currentRequestType: String = ""
)
