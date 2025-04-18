package com.example.restaurantapp.ui.admin

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.restaurantapp.ui.components.MenuItemForm
import com.example.restaurantapp.ui.components.LoadingScreen
import com.example.restaurantapp.ui.components.ErrorScreen
import com.example.restaurantapp.ui.components.SuccessScreen

@Composable
fun EditMenuItemScreen(navController: NavController, itemId: String) {
    val db = FirebaseFirestore.getInstance()
    var item by remember { mutableStateOf<MenuItem?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var saveSuccess by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        try {
            val snapshot = db.collection("menuItems").document(itemId).get().await()
            item = snapshot.toObject(MenuItem::class.java)?.copy(id = itemId)
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to load item."
        } finally {
            loading = false
        }
    }

    when {
        loading -> LoadingScreen("Fetching item details...")
        isSaving -> LoadingScreen("Saving changes...")
        saveSuccess -> SuccessScreen(
            message = "Menu item updated!",
            onDismiss = { navController.popBackStack() }
        )
        error != null -> ErrorScreen(
            message = error ?: "Unknown error",
            onRetry = {
                error = null
                loading = true
            }
        )
        item != null -> {
            MenuItemForm(
                initialItem = item!!,
                onSubmit = { updatedItem ->
                    isSaving = true
                    db.collection("menuItems").document(itemId).set(updatedItem)
                        .addOnSuccessListener {
                            isSaving = false
                            saveSuccess = true
                        }
                        .addOnFailureListener {
                            isSaving = false
                            error = it.localizedMessage ?: "Failed to save changes."
                        }
                }
            )
        }
        else -> {
            ErrorScreen(message = "Item not found.", onRetry = { navController.popBackStack() })
        }
    }
}
