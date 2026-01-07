package com.example.projekatfaza23.model

import com.example.projekatfaza23.data.LeaveRequest
import com.example.projekatfaza23.data.RequestSatus
import kotlinx.coroutines.delay

class FakeLeaveRepository : LeaveRepository {
    override suspend fun getLeaveHistory(): List<LeaveRequest> {
        delay(500)

        return listOf(
            SingleLeaveRequest(1),
            SingleLeaveRequest(2),
            SingleLeaveRequest(3),
            SingleLeaveRequest(4),
            SingleLeaveRequest(5)
        )
    }
    override suspend fun submitNewRequest(request: LeaveRequest): Boolean {
        delay(500)
        return true
    }
}

fun SingleLeaveRequest(id: Int) : LeaveRequest {
    return LeaveRequest(
        id = id,
        status = RequestSatus.entries[(1..2).random()],
        type = "Annual leave",
        explanation = " ",
        fileName = "",
        dateFrom = "Dec",
        dateTo = "Jan"
    )
}