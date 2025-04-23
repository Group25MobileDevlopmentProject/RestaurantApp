package com.example.restaurantapp

import android.app.Application
import com.example.restaurantapp.util.NotificationHelper

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Create the notification channel
        NotificationHelper.createNotificationChannel(applicationContext)
    }
}