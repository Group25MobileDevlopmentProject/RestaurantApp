package com.example.restaurantapp

data class MenuItem(
    val id: Int,
    val category: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageResId: Int,
    val isVegetarian: Boolean,
    val isGlutenFree: Boolean
)