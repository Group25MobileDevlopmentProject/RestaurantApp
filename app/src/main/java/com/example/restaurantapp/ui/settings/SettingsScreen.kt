package com.example.restaurantapp.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.theme.IrishGreen

@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IrishGreen,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Appearance
        SettingsSection(title = "Appearance") {
            SettingsSwitchOption(
                title = "Dark Mode",
                isChecked = isDarkMode,
                onToggle = onDarkModeChanged
            )
        }

        // Notifications
        SettingsSection(title = "Notifications") {
            SettingsSwitchOption(
                title = "Enable Notifications",
                isChecked = true,
                onToggle = { /* TODO */ }
            )
        }

        // Language
        SettingsSection(title = "Language") {
            SettingsRowOption(
                title = "Change Language",
                onClick = { navController.navigate("language_selection") }
            )
        }

        // Help
        SettingsSection(title = "Help & Support") {
            SettingsRowOption(title = "FAQ") { /* TODO */ }
            SettingsRowOption(title = "Contact Support") { /* TODO */ }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save button (optional)
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = IrishGreen)
        ) {
            Text("Save Settings", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = IrishGreen,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
    content()
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 1.dp,
        color = Color.LightGray
    )
}

@Composable
fun SettingsSwitchOption(
    title: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = IrishGreen,
                checkedTrackColor = IrishGreen.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

@Composable
fun SettingsRowOption(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
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
