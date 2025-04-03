package com.example.restaurantapp.ui.model

data class MenuItem(
    val id: String = "", // Unique Firestore document ID
    val category: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val isVegetarian: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isVegan: Boolean = false
)
