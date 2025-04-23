package com.example.restaurantapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.util.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    navController: NavController,
    preferencesManager: PreferencesManager,
    snackbarHostState: SnackbarHostState
) {
    val languageMap = mapOf(
        "en" to "English",
        "fi" to "Finnish",
        "es" to "Spanish",
        "fr" to "French"
    )
    val languages = languageMap.values.toList()

    val currentLanguageCode by preferencesManager.languageFlow.collectAsState(initial = "en")
    val currentLanguage = languageMap[currentLanguageCode] ?: "English"

    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    LaunchedEffect(currentLanguage) {
        selectedLanguage = currentLanguage
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(paddingValues)
        ) {

            TopAppBar(
                title = { Text("Select Language") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            languages.forEach { language ->
                LanguageOption(
                    language = language,
                    isSelected = selectedLanguage == language,
                    onClick = { selectedLanguage = language }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val selectedCode = languageMap.entries
                            .firstOrNull { it.value == selectedLanguage }
                            ?.key ?: "en"

                        preferencesManager.setLanguage(selectedCode)

                        // Navigate back first
                        navController.popBackStack()

                        // Then show Snackbar (hosted at app level, so still visible)
                        snackbarHostState.showSnackbar(
                            message = "$selectedLanguage saved!",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Language", color = Color.White)
            }
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
