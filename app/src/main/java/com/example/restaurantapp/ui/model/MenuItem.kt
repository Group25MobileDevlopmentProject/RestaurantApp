package com.example.restaurantapp.ui.model

data class MenuItem(
    val id: String = "",               // Unique Firestore document ID
    val category: String = "",         // e.g., "Drinks", "Main Course"
    val name: String = "",             // e.g., "Whiskey Sour"
    val price: Double = 0.0,           // e.g., 12.99
    val description: String = "",      // Details for the customer
    val imageUrl: String = "",         // Image URL from Firebase Storage or CDN
    val isVegetarian: Boolean = false, // True if suitable for vegetarians
    val isGlutenFree: Boolean = false, // True if gluten-free
    val isVegan: Boolean = false,      // True if vegan
    val tags: List<String> = emptyList(), // Flexible metadata (e.g., ["whiskey", "cocktail", "sweet"])
    val isFeatured: Boolean = false,   // Highlighted on the menu
    val isAvailable: Boolean = true    // True if currently orderable
)