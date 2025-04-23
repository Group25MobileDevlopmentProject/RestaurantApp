package com.example.restaurantapp.ui.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
    val tags: List<String> = emptyList(),
    val capacity: Int? = null,
    val rsvpUserIds: List<String> = emptyList(),  // List of attending user IDs
    val isFeatured: Boolean = false,
    val isPublic: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null
) {

    // Add status field to track if the event is upcoming or past
    val status: String
        get() = if (date.toDate().after(java.util.Date())) "upcoming" else "past"
}