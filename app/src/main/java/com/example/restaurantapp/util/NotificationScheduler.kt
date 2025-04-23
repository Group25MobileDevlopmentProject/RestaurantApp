package com.example.restaurantapp.util

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import com.example.restaurantapp.workers.EventNotificationWorker

object NotificationScheduler {

    fun scheduleEventNotification(context: Context, eventTitle: String, eventDescription: String, delayInMillis: Long) {
        // Create input data for the worker
        val inputData = Data.Builder()
            .putString("eventTitle", eventTitle)
            .putString("eventDescription", eventDescription)
            .build()

        // Create a OneTimeWorkRequest
        val workRequest = OneTimeWorkRequest.Builder(EventNotificationWorker::class.java)
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        // Enqueue the work request
        WorkManager.getInstance(context).enqueueUniqueWork(
            "event_notification_${eventTitle}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}