package com.example.restaurantapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantapp.ui.model.MenuItem

@Composable
fun MenuItemForm(
    initialItem: MenuItem = MenuItem("", "", "", 0.0, "", "", false, false, false, emptyList(), false),
    onSubmit: (MenuItem) -> Unit
) {
    var name by remember { mutableStateOf(initialItem.name) }
    var description by remember { mutableStateOf(initialItem.description) }
    var price by remember { mutableStateOf(initialItem.price.toString()) }
    var imageUrl by remember { mutableStateOf(initialItem.imageUrl) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price (€)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val parsedPrice = price.toDoubleOrNull() ?: 0.0
                onSubmit(
                    initialItem.copy(
                        name = name,
                        description = description,
                        price = parsedPrice,
                        imageUrl = imageUrl
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
