package com.example.restaurantapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Appearance Section
        SettingsSection(title = "Appearance") {
            SettingsOption(
                title = "Dark Mode",
                toggleState = isDarkMode,
                onToggleChanged = onDarkModeChanged,
                buttonLabel = "Toggle Dark Mode" // Custom label for dark mode toggle
            )
        }

        // Notifications Section
        SettingsSection(title = "Notifications") {
            SettingsOption(
                title = "Enable Notifications",
                toggleState = true,
                onToggleChanged = {},
                buttonLabel = "Enable Notifications" // Custom label for notifications toggle
            )
        }

        // Language Section
        SettingsSection(title = "Language") {
            SettingsOption(
                title = "Change Language",
                onClick = { navController.navigate("language_selection") },
                buttonLabel = "Select Language" // Custom label for language selection
            )
        }

        // Help Section
        SettingsSection(title = "Help & Support") {
            SettingsOption(
                title = "FAQ",
                onClick = { /* Navigate to FAQ */ },
                buttonLabel = "Read FAQ" // Custom label for FAQ
            )
            SettingsOption(
                title = "Contact Support",
                onClick = { /* Navigate to support page */ },
                buttonLabel = "Get Support" // Custom label for contact support
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button (optional)
        Button(
            onClick = { /* Handle save settings */ },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Save Settings")
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    // Section Title
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    // Section Content
    content()
}

@Composable
fun SettingsOption(
    title: String,
    toggleState: Boolean = false,
    onToggleChanged: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    buttonLabel: String = "Change" // Default label for buttons
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp)

        // For toggle buttons like Dark Mode
        if (title == "Dark Mode") {
            Switch(
                checked = toggleState,
                onCheckedChange = onToggleChanged
            )
        } else {
            Button(onClick = onClick) {
                Text(text = buttonLabel) // Use the button label here
            }
        }
    }
}