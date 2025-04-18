package com.example.restaurantapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.theme.IrishGreen

@Composable
fun AdminScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header matching SettingsScreen style
        Text(
            text = "Admin Panel",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IrishGreen,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Admin Options
        AdminOption(title = "Manage Menu Items") {
            navController.navigate("admin_manage_menu")
        }
        AdminOption(title = "Manage Events") {
            navController.navigate("admin_manage_events")
        }
        AdminOption(title = "Send Notifications") {
            navController.navigate("admin_send_notifications")
        }
        AdminOption(title = "View Orders") {
            navController.navigate("admin_view_orders")
        }
        AdminOption(title = "Settings") {
            navController.navigate("settings")
        }
    }
}

@Composable
fun AdminOption(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = IrishGreen
            )
        }
    }
}
