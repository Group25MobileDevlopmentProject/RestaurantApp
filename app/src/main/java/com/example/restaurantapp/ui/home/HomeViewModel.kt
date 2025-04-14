package com.example.restaurantapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.restaurantapp.ui.model.Event
import com.example.restaurantapp.ui.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.*

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var featuredItems by mutableStateOf<List<MenuItem>>(emptyList())
        private set

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    init {
        fetchFeaturedMenuItems()
        fetchUpcomingEvents()
    }

    private fun fetchFeaturedMenuItems() {
        db.collection("menuItems")
            .whereEqualTo("isFeatured", true)
            .get()
            .addOnSuccessListener { result ->
                featuredItems = result.toObjects(MenuItem::class.java)
            }
    }

    private fun fetchUpcomingEvents() {
        db.collection("events")
            .whereEqualTo("isFeatured", true)
            .get()
            .addOnSuccessListener { result ->
                events = result.toObjects(Event::class.java)
            }
    }
}
