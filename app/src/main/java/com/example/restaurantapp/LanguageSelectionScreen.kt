package com.example.restaurantapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(navController: NavController) {
    // Available languages
    val languages = listOf("English", "Finnish", "Spanish", "French")

    // State to keep track of the selected language
    var selectedLanguage by remember { mutableStateOf(languages[0]) }

    // Language selection layout
    Column(modifier = Modifier.padding(16.dp)) {
        // TopAppBar with Back Button
        TopAppBar(
            title = { Text("Select Language") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Display language options
        languages.forEach { language ->
            LanguageOption(
                language = language,
                isSelected = selectedLanguage == language,
                onClick = {
                    selectedLanguage = language
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button (optional)
        Button(
            onClick = { /* Handle language change saving logic */ },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Save Language", color = Color.White)
        }
    }
}


@Composable
fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
