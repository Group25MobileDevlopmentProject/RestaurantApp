package com.example.restaurantapp.ui.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Normalizer

@Composable
fun AddMenuItemScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var category by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isVegetarian by remember { mutableStateOf(false) }
    var isGlutenFree by remember { mutableStateOf(false) }
    var isVegan by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }

    var showConfirmation by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Confirm Add") },
            text = { Text("Are you sure you want to save this menu item?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        saveMenuItem(
                            db, context, navController,
                            category, name, price, description,
                            imageUrl, isVegetarian, isGlutenFree,
                            isVegan, tags, isFeatured
                        ) {
                            // Clear form on success
                            category = ""
                            name = ""
                            price = ""
                            description = ""
                            imageUrl = ""
                            isVegetarian = false
                            isGlutenFree = false
                            isVegan = false
                            tags = ""
                            isFeatured = false
                        }
                    }
                ) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price (€)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
        OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma-separated)") })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isVegetarian, onCheckedChange = { isVegetarian = it })
            Text("Vegetarian")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isGlutenFree, onCheckedChange = { isGlutenFree = it })
            Text("Gluten-Free")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isVegan, onCheckedChange = { isVegan = it })
            Text("Vegan")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
            Text("Featured Item")
        }

        Button(
            onClick = {
                when {
                    category.isBlank() -> {
                        Toast.makeText(context, "Please fill in the category.", Toast.LENGTH_SHORT).show()
                    }
                    name.isBlank() -> {
                        Toast.makeText(context, "Please fill in the name.", Toast.LENGTH_SHORT).show()
                    }
                    price.isBlank() || price.toDoubleOrNull() == null -> {
                        Toast.makeText(context, "Please enter a valid price.", Toast.LENGTH_SHORT).show()
                    }
                    description.isBlank() -> {
                        Toast.makeText(context, "Please fill in the description.", Toast.LENGTH_SHORT).show()
                    }
                    imageUrl.isBlank() -> {
                        Toast.makeText(context, "Please provide an image URL.", Toast.LENGTH_SHORT).show()
                    }
                    tags.isBlank() -> {
                        Toast.makeText(context, "Please provide at least one tag.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        showConfirmation = true
                    }
                }
            },
            enabled = !isSaving
        ) {
            Text(if (isSaving) "Saving..." else "Save Item")
        }

    }
}

private fun saveMenuItem(
    db: FirebaseFirestore,
    context: android.content.Context,
    navController: NavController,
    category: String,
    name: String,
    price: String,
    description: String,
    imageUrl: String,
    isVegetarian: Boolean,
    isGlutenFree: Boolean,
    isVegan: Boolean,
    tags: String,
    isFeatured: Boolean,
    onSuccess: () -> Unit
) {
    val id = generateMenuIdFromName(name)

    // Check for duplicates first
    db.collection("menuItems").document(id).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                Toast.makeText(context, "An item with this name already exists!", Toast.LENGTH_LONG).show()
            } else {
                val newMenuItem = MenuItem(
                    id = id,
                    category = category,
                    name = name,
                    price = price.toDouble(),
                    description = description,
                    imageUrl = imageUrl,
                    isVegetarian = isVegetarian,
                    isGlutenFree = isGlutenFree,
                    isVegan = isVegan,
                    tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    isFeatured = isFeatured
                )

                db.collection("menuItems")
                    .document(id)
                    .set(newMenuItem)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Menu item added!", Toast.LENGTH_SHORT).show()
                        onSuccess()  // Clear form
                        navController.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error checking duplicates: ${it.message}", Toast.LENGTH_LONG).show()
        }
}

fun generateMenuIdFromName(name: String): String {
    return Normalizer.normalize(name, Normalizer.Form.NFD)
        .replace("[^\\p{ASCII}]".toRegex(), "") // remove accents
        .lowercase()
        .replace("\\s+".toRegex(), "_") // replace spaces with underscores
        .replace("[^a-z0-9_]".toRegex(), "") // remove any non a-z, 0-9, _
}