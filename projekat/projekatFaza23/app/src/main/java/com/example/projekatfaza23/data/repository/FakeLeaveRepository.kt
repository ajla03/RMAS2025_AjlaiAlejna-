package com.example.projekatfaza23.model

import com.example.projekatfaza23.data.auth.UserManager
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLeaveRepository : LeaveRepositoryI {
    override fun getLeaveHistory(): Flow<List<LeaveRequest>> = flow {
        delay(500)

        val fakeData = listOf(
            SingleLeaveRequest("1", 1000L),
            SingleLeaveRequest("2", 2000L),
            SingleLeaveRequest("3", 3000L),
            SingleLeaveRequest("4", 4000L),
            SingleLeaveRequest("5", 5000L)
        )
        emit(fakeData)
    }

      fun getLeaveHistorySync(): List<LeaveRequest>  {
         val fakeData = listOf(
             SingleLeaveRequest("1", 1000L),
             SingleLeaveRequest("2", 2000L),
             SingleLeaveRequest("3", 3000L),
             SingleLeaveRequest("4", 4000L),
             SingleLeaveRequest("5", 5000L)
        )
        return fakeData
    }

    override suspend fun submitNewRequest(request: LeaveRequest): Boolean {
        delay(500)
        return true
    }
}

fun SingleLeaveRequest(id: String, dateFrom : Long) : LeaveRequest {
    return LeaveRequest(
        id = id,
        status = RequestSatus.entries[(1..2).random()],
        type = "Annual leave",
        explanation = " ",
        userEmail = UserManager.currentUser.value?.email ?: "",
        leave_dates = listOf<LeaveDates>(
            LeaveDates(com.google.firebase.Timestamp(java.util.Date(dateFrom)), Timestamp(0L,0))
        ),
        file_info = FileInfo("", "", "")
    )
}