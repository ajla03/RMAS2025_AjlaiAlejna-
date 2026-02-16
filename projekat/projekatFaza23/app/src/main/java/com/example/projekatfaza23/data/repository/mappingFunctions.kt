package com.example.projekatfaza23.data.repository

import com.example.projekatfaza23.data.auth.UserProfile
import com.example.projekatfaza23.data.db.FileInfoSimple
import com.example.projekatfaza23.data.db.LeaveDateLDB
import com.example.projekatfaza23.data.db.LeaveRequestEntity
import com.example.projekatfaza23.data.db.UserEntity
import com.example.projekatfaza23.model.FileInfo
import com.example.projekatfaza23.model.LeaveDates
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import com.google.firebase.Timestamp


fun LeaveRequest.toEntity() : LeaveRequestEntity {
    return LeaveRequestEntity(
        id = this.id,
        userEmail = this.userEmail,
        type = this.type,
        explanation = this.explanation,
        explanationDean = this.explanationDean,
        explanationSecretary = this.explanationSecretary,
        status = this.status.name,
        leaveDates = this.leave_dates?.map {
            date ->
            LeaveDateLDB (
                startMs = date?.start?.toDate()?.time,
                endMs = date?.end?.toDate()?.time
            )
        },
        createdAt = this.createdAt?.seconds?.times(1000),
        fileInfo = this.file_info?.let {
            FileInfoSimple(it.file_name, it.file_type, it.uri)
        }
    )
}
fun LeaveRequestEntity.toLeaveRequest(): LeaveRequest {
    return LeaveRequest(
        id = this.id,
        userEmail = this.userEmail,
        type = this.type,
        explanation = this.explanation ?: "",
        explanationDean = this.explanationDean ?: "",
        explanationSecretary = this.explanationSecretary ?: "",
        status = RequestSatus.valueOf(this.status),
        leave_dates = this.leaveDates?.map {
            entry -> LeaveDates (
                start = entry?.startMs?.let { ms ->
                    Timestamp(java.util.Date(ms))
                },
            end = entry?.endMs?.let { ms ->
                Timestamp(java.util.Date(ms))
                }
            )
        },
        createdAt = this.createdAt?.let { com.google.firebase.Timestamp(it / 1000, 0) },
        file_info = this.fileInfo?.let {
            FileInfo(it.fileName, it.fileType, it.fileUri)
        }
    )
}