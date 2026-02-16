package com.example.projekatfaza23.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.projekatfaza23.model.FileInfo
import com.example.projekatfaza23.model.LeaveDates
import com.example.projekatfaza23.model.RequestSatus

@Entity(tableName = "leave_request")
data class LeaveRequestEntity(
    @PrimaryKey val id: String, //dokument sa firestore
    val userEmail: String,
    val type: String,
    val explanation: String?,

    val explanationDean: String?,
    val explanationSecretary: String?,

    val status: String,

    val leaveDates: List<LeaveDateLDB?>?,

    @Embedded(prefix = "file_")
    val fileInfo: FileInfoSimple?,
    val createdAt: Long? = null
)

data class FileInfoSimple(
    val fileName: String,
    val fileType: String,
    val fileUri: String
)

data class LeaveDateLDB(
    val startMs: Long? = null,
    val endMs: Long? = null
)