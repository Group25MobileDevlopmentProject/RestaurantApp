package com.example.restaurantapp.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.restaurantapp.util.NotificationHelper

class EventNotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // Retrieve event data from input data or other source
        val eventTitle = inputData.getString("eventTitle") ?: "Event"
        val eventDescription = inputData.getString("eventDescription") ?: "Event Description"

        // Send the notification
        NotificationHelper.sendEventNotification(applicationContext, eventTitle, eventDescription)

        return Result.success()
    }
}