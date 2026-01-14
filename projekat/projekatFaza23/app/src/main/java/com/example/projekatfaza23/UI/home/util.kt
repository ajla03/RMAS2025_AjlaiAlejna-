package com.example.projekatfaza23.UI.home

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.SimpleFormatter

fun formatTimestampToDate (timestamp: Timestamp?) : String{
    if(timestamp == null){
        return "Date"
    }
    val date = timestamp.toDate()
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
}