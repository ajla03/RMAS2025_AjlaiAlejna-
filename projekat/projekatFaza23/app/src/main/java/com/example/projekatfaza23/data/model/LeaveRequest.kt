package com.example.projekatfaza23.model

enum class RequestSatus{
    Pending,
    Approved,
    Denied
}

data class LeaveRequest(
    val id: Int = 0,
    val status: RequestSatus = RequestSatus.Pending,
    val type: String  = "",
    val explanation: String ="",
    val fileName: String = "",
    val dateFrom: String = "",
    val dateTo: String =  ""
)
