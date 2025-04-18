package com.example.restaurantapp.ui.admin

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import com.example.restaurantapp.ui.components.LoadingScreen
import com.example.restaurantapp.ui.components.ErrorScreen
import com.example.restaurantapp.ui.components.SuccessScreen
import com.example.restaurantapp.ui.components.MenuItemForm

@Composable
fun AddMenuItemScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var isSaving by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    when {
        isSaving -> LoadingScreen("Saving new menu item...")
        saveSuccess -> SuccessScreen(
            message = "Menu item added!",
            onDismiss = { navController.popBackStack() }
        )
        error != null -> ErrorScreen(
            message = error ?: "Unknown error",
            onRetry = { error = null }
        )
        else -> {
            MenuItemForm(
                onSubmit = { newItem ->
                    isSaving = true
                    val itemId = UUID.randomUUID().toString()
                    db.collection("menuItems").document(itemId).set(
                        newItem.copy(id = itemId)
                    ).addOnSuccessListener {
                        isSaving = false
                        saveSuccess = true
                    }.addOnFailureListener {
                        isSaving = false
                        error = it.localizedMessage ?: "Failed to save menu item."
                    }
                }
            )
        }
    }
}
