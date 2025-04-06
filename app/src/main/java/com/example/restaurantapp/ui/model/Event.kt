package com.example.restaurantapp.ui.model

import com.google.firebase.Timestamp

data class Event(
    val title: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
    val tags: List<String> = emptyList(),
    val isFeatured: Boolean = false
)
