package com.example.projekatfaza23.data.db

import androidx.room.TypeConverter
import com.example.projekatfaza23.model.LeaveDates
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLeaveDatesList(value: List<LeaveDates>?) : String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLeaveDatesList(value: String): List<LeaveDates?>? {
        val listType = object : TypeToken<List<LeaveDates?>>() {}.type
        return gson.fromJson(value, listType)
    }
}