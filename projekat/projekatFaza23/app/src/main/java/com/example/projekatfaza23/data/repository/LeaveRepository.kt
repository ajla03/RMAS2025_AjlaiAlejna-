package com.example.projekatfaza23.model


interface LeaveRepository {
    suspend fun getLeaveHistory() : List<LeaveRequest>
    suspend fun submitNewRequest(request: LeaveRequest) : Boolean
}