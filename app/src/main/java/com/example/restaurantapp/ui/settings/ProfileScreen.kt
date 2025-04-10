package com.example.restaurantapp.ui.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.R
import com.example.restaurantapp.ui.theme.IrishGreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(navController: NavController, user: FirebaseUser?) {
    val context = LocalContext.current
    val userName = user?.displayName ?: "User"
    val userEmail = user?.email ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IrishGreen,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .align(Alignment.Start)
        )

        // Profile Picture Centered
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_profile_pic),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }

        // User Info
        Text(
            text = "Name",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = userName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "Email",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = userEmail,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Settings Options
        ProfileSettingItem(title = "Change Email", icon = Icons.Default.Email) {
            // Handle Change Email
        }
        ProfileSettingItem(title = "Change Password", icon = Icons.Default.Lock) {
            // Handle Change Password
        }
        ProfileSettingItem(title = "Log Out", icon = Icons.AutoMirrored.Filled.ExitToApp) {
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
fun ProfileSettingItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = IrishGreen
            )
        },
        headlineContent = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    )
    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
}
