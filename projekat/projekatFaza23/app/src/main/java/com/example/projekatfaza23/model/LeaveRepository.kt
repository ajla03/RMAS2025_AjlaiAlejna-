package com.example.projekatfaza23.model

import kotlinx.coroutines.delay

interface LeaveRepository {
    suspend fun getLeaveHistory() : List<LeaveRequest>
    suspend fun submitNewRequest(request: LeaveRequest) : Boolean
}