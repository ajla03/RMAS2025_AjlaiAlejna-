package com.example.projekatfaza23.model

import android.net.Uri
import com.google.firebase.Timestamp

enum class RequestSatus{
    Pending,
    PendingDean,
    Approved,
    Denied
}

data class LeaveRequest(
    val id: String = "",
    val status: RequestSatus = RequestSatus.Pending,
    val type: String = "",
    val explanation: String = "",
    val explanationDean : String = "",
    val explanationSecretary : String = "",
    val userEmail: String = "",

    val leave_dates: List<LeaveDates?>? = null,

    val file_info: FileInfo? = null,
    val createdAt: Timestamp? = null
)

data class LeaveDates(
    val start: Timestamp? = null,
    val end: Timestamp? = null
)

data class FileInfo(
    val file_name: String = "",
    val file_type: String = "",
    val uri: String = ""
)