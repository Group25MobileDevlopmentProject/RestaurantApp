package com.example.restaurantapp.ui.settings

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.alpha

@Composable
fun ProfileScreen(navController: NavController, user: FirebaseUser?) {
    val context = LocalContext.current
    val userName = user?.displayName ?: "User"
    val userEmail = user?.email ?: ""

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // Profile Picture
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = R.drawable.default_profile_pic), contentDescription = "Profile Picture",
                modifier = Modifier.size(120.dp).clip(CircleShape))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Name: $userName", fontSize = 20.sp)
        Text(text = "Email: $userEmail", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()

        // Settings Options
        SettingsOption("Change Email", Icons.Default.Email) {}
        SettingsOption("Change Password", Icons.Default.Lock) {}
        SettingsOption("Log Out", Icons.AutoMirrored.Filled.ExitToApp) {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            navController.navigate("welcome") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun SettingsOption(title: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp),
        leadingContent = { Icon(imageVector = icon, contentDescription = title) },
        headlineContent = { Text(text = title, fontSize = 18.sp) }
    )
    HorizontalDivider()
}
