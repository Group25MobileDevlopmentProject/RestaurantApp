package com.example.restaurantapp.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.theme.IrishGreen
import com.example.restaurantapp.ui.theme.LightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, cartItems: List<MenuItem>) {
    var name by remember { mutableStateOf("") }
    var tableNumber by remember { mutableStateOf("") }

    val total = cartItems.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Checkout", fontSize = 28.sp, color = IrishGreen)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Order Summary", fontSize = 20.sp, color = IrishGreen)

            Spacer(modifier = Modifier.height(8.dp))
            cartItems.forEach { item ->
                Text("- ${item.name} (€${item.price})")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Total: €$total", fontSize = 18.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = tableNumber,
                onValueChange = { tableNumber = it },
                label = { Text("Table Number") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                // Handle order placement
                navController.navigate("order_status")
            },
            colors = ButtonDefaults.buttonColors(containerColor = IrishGreen),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Confirm Order", color = Color.White, fontSize = 18.sp)
        }
    }
}
