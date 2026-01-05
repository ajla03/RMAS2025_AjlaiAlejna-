package com.example.projekatfaza23.model

import kotlinx.coroutines.delay

interface LeaveRepository {
    suspend fun getLeaveHistory() : List<LeaveRequest>
    suspend fun submitNewRequest(request: LeaveRequest) : Boolean
}

class FakeLeaveRepository : LeaveRepository{
    override suspend fun getLeaveHistory(): List<LeaveRequest> {
        delay(500)

        return listOf(
            LeaveRequest(
                id = 1,
                status = "Pending",
                type = "Personal days",
                dateFrom = "Nov 13th",
                dateTo = "Nov 15th"
            ),
            LeaveRequest(
                id = 2,
                status = "Approved",
                type = "Working from home",
                dateFrom = "Nov 3rd",
                dateTo = "Nov 3rd"
            ),
            LeaveRequest(
                id = 3,
                status = "Denied",
                type = "Personal days",
                dateFrom = "Sept 25th",
                dateTo = "Sept 28th"
            ),
            LeaveRequest(
                id = 4,
                status = "Approved",
                type = "Sick leave",
                dateFrom = "Oct 3rd",
                dateTo = "Oct 5th"
            )
        )
    }

    override suspend fun submitNewRequest(request: LeaveRequest): Boolean {
        delay(500)
        return true
    }
}