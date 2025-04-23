package com.example.restaurantapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.restaurantapp.R
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object NotificationHelper {

    private const val CHANNEL_ID = "events_channel"
    private const val CHANNEL_NAME = "Event Notifications"
    private const val NOTIFICATION_ID = 1

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = "Channel for event notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendEventNotification(context: Context, eventTitle: String, eventDescription: String) {
        // Check if the permission to post notifications is granted
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Set your app's icon
                .setContentTitle(eventTitle)
                .setContentText(eventDescription)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            // If permission is not granted, you can request it (from an Activity or Fragment)
            // For simplicity, you may handle permission requests outside this function
            // Example: ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
        }
    }
}