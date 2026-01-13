package com.example.projekatfaza23.model

import android.net.Uri

enum class RequestSatus{
    Pending,
    Approved,
    Denied
}


data class LeaveRequest(
    val id: String = "",
    val status: RequestSatus = RequestSatus.Pending,
    val type: String  = "",
    val explanation: String ="",
    val fileName: String = "",
    val fileUri : String? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val userEmail : String = ""
)
