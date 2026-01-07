package com.example.projekatfaza23.model

import com.example.projekatfaza23.data.LeaveRequest

interface LeaveRepository {
    suspend fun getLeaveHistory() : List<LeaveRequest>
    suspend fun submitNewRequest(request: LeaveRequest) : Boolean
}