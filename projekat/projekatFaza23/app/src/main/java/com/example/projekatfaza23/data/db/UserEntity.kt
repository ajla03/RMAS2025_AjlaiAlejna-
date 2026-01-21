package com.example.projekatfaza23.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.projekatfaza23.UI.Role
import com.example.projekatfaza23.UI.home.Status
import java.sql.Struct

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email : String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String?,
    val totalDays: Int = 20,
    val usedDays: Int,
    val userStatus: String,
    val role: String = Status.AtWork.name
)
