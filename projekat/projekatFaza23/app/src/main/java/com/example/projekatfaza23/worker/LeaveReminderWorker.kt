package com.example.projekatfaza23.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.projekatfaza23.R

class LeaveReminderWorker (
    private val context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result{
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "leave_remindersv2"

        val requestId = inputData.getString("request_id") ?: "unknown"
        val endDateMillis = inputData.getLong("end_date_millis", 0L)
        val currentTime = System.currentTimeMillis()

        // zaustavljanje periodic work managera
        if (endDateMillis != 0L && currentTime > endDateMillis) {
            WorkManager.getInstance(context).cancelUniqueWork("periodic_reminder_$requestId")
            return Result.success()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Podsjetnici za povratak na posao",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kanal za podsjetnike o isteku leava "
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.hrapp_logo)  // mogu staviti nasu sliku logoa
            .setContentTitle("Godišnji odmor ističe!")
            .setContentText("Vaš odmor uskoro završava. Ne zaboravite se vratiti na posao.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)  // sistemski zvuk
            .setAutoCancel(true)
            .build()

        notificationManager.notify(requestId.hashCode(), notification)

        return Result.success()
    }
}