package com.example.projekatfaza23.data.repository

import com.example.projekatfaza23.data.auth.UserProfile
import com.example.projekatfaza23.data.db.FileInfoSimple
import com.example.projekatfaza23.data.db.LeaveRequestEntity
import com.example.projekatfaza23.data.db.UserEntity
import com.example.projekatfaza23.model.FileInfo
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus

fun mapToEntity(googleProfile: UserProfile,
                role: String,
                status: String,
                total: Int,
                used: Int) : UserEntity {
    return UserEntity(
        email = googleProfile.email ?: "",
        firstName = googleProfile.name ?: "",
        lastName = googleProfile.lastName ?: "",
        imageUrl = googleProfile.profilePictureURL.toString(),
        role = role,
        totalDays = total,
        usedDays = used,
        userStatus = status
    )
}

fun LeaveRequest.toEntity() : LeaveRequestEntity {
    return LeaveRequestEntity(
        id = this.id,
        userEmail = this.userEmail,
        type = this.type,
        explanation = this.explanation,
        status = this.status.name,
        leaveDates = this.leave_dates,
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
        status = RequestSatus.valueOf(this.status),
        leave_dates = this.leaveDates ?: emptyList(),
        createdAt = this.createdAt?.let { com.google.firebase.Timestamp(it / 1000, 0) },
        file_info = this.fileInfo?.let {
            FileInfo(it.fileName, it.fileType, it.fileUri)
        }
    )
}