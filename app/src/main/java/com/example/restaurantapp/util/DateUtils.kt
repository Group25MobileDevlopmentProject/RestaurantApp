package com.example.restaurantapp.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun formatTimestamp(timestamp: Timestamp): String {
    return try {
        val date = timestamp.toDate()
        SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        "Invalid date"
    }
}
