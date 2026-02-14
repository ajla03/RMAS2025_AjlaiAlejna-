package com.example.projekatfaza23.UI.secretary

import com.example.projekatfaza23.data.db.UserEntity
import com.example.projekatfaza23.model.LeaveRequest

data class SecretaryUIState(
    val allRequests: List<LeaveRequest> = emptyList(),
    val employees: List<UserEntity> = emptyList(),

    val displayedRequests: List<LeaveRequest> = emptyList(),

    val selectedRequest: LeaveRequest? = null,
    val requestAuthor: UserEntity? = null,
    val explanationSecretary: String = "",

    val stats: UserVacationStats = UserVacationStats(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class UserVacationStats(
    val totalDays: Int = 0,
    val usedDays: Int = 0,
    val pendingDays: Int = 0, // dani u zahtjevu
    val remainingDays: Int = 0
)
