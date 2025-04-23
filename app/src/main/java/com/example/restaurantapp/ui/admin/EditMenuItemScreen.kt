package com.example.restaurantapp.ui.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditMenuItemScreen(
    navController: NavController,
    itemId: String
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var isLoading by remember { mutableStateOf(true) }

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

    // Fetch item from Firestore on first composition
    LaunchedEffect(itemId) {
        db.collection("menuItems").document(itemId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    category = document.getString("category") ?: ""
                    name = document.getString("name") ?: ""
                    price = document.getDouble("price")?.toString() ?: ""
                    description = document.getString("description") ?: ""
                    imageUrl = document.getString("imageUrl") ?: ""
                    isVegetarian = document.getBoolean("isVegetarian") == true
                    isGlutenFree = document.getBoolean("isGlutenFree") == true
                    isVegan = document.getBoolean("isVegan") == true
                    isFeatured = document.getBoolean("isFeatured") == true
                    tags = (document.get("tags") as? List<*>)?.joinToString(", ") ?: ""
                }
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error loading item: ${it.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Normal TextFields for category, name, price, etc.
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (€)") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
            OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma-separated)") })

            // Boolean checkboxes reflect Firestore values
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

            // Save button
            Button(onClick = {
                if (name.isNotBlank() && price.toDoubleOrNull() != null) {

                    val updatedMenuItem = hashMapOf(
                        "id" to itemId,
                        "category" to category,
                        "name" to name,
                        "price" to price.toDouble(),
                        "description" to description,
                        "imageUrl" to imageUrl,
                        "isVegetarian" to isVegetarian,
                        "isGlutenFree" to isGlutenFree,
                        "isVegan" to isVegan,
                        "tags" to tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        "isFeatured" to isFeatured
                    )

                    db.collection("menuItems").document(itemId)
                        .set(updatedMenuItem)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Menu item updated!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }

                } else {
                    Toast.makeText(context, "Please fill in the name and a valid price.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save Changes")
            }
        }
    }
}