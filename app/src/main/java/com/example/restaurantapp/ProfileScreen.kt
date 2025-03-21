package com.example.restaurantapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(navController: NavController) {
    // Sample data for profile
    var userName by remember { mutableStateOf("John Doe") }
    var userEmail by remember { mutableStateOf("johndoe@example.com") }
    var profilePicUri by remember { mutableStateOf("uri_to_profile_pic") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Profile Picture
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val circleShape = CircleShape  // Use CircleShape here for a circular profile picture
            Image(
                painter = painterResource(id = R.drawable.default_profile_pic),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(120.dp).clip(circleShape)  // Apply the circle shape for the profile image
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display User Name and Email
        Text(text = "Name: $userName", fontSize = 20.sp)
        Text(text = "Email: $userEmail", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Account Actions: Change Email, Change Password, Log Out
        SettingsOption(title = "Change Email", onClick = { /* Handle email change */ })
        SettingsOption(title = "Change Password", onClick = { /* Handle password change */ })
        SettingsOption(title = "Log Out", onClick = { /* Handle log out */ })
    }
}

@Composable
fun SettingsOption(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp)
        Button(onClick = onClick) {
            Text("Edit")
        }
    }
}
