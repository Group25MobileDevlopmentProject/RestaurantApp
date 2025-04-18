package com.example.restaurantapp.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.R
import com.example.restaurantapp.ui.theme.IrishGreen

@Composable
fun OrderConfirmationScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success Image
        Image(
            painter = painterResource(id = R.drawable.success_tick), // Add a tick/checkmark image to your `res/drawable`
            contentDescription = "Order Confirmed",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Order Confirmed!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = IrishGreen
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Thank you for your order.\nYour food will be ready shortly.",
            fontSize = 16.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = IrishGreen)
        ) {
            Text("Back to Home", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
