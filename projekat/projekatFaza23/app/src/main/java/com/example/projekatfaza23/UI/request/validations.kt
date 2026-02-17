package com.example.projekatfaza23.UI.request

import java.util.Calendar
import androidx.compose.material3.CenterAlignedTopAppBar
import com.google.firebase.Timestamp

object validationHelpers {

    fun countWorkDays(start: Timestamp, end: Timestamp) : Int {
        var workDays = 0

        val startCalendar = Calendar.getInstance().apply {
            timeInMillis = start.toDate().time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

        }

        val ednCalendar = Calendar.getInstance().apply {
            timeInMillis = end.toDate().time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

        }

        if (startCalendar.timeInMillis > ednCalendar.timeInMillis) return 0

        while (startCalendar.timeInMillis <= ednCalendar.timeInMillis) {
            val dayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK)

            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                workDays++
            }
            startCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return workDays
    }
}