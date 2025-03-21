package com.example.restaurantapp

import MenuItemInfoPage
import MenuOrderingPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.restaurantapp.ui.theme.RestaurantAppTheme
import androidx.compose.material.icons.filled.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_RestaurantApp)

        // Set up the Composable content
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    var isDarkMode by remember { mutableStateOf(false) } // State for dark mode

    RestaurantAppTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState()

        // Scaffold with bottom bar visibility based on route
        Scaffold(
            bottomBar = {
                if (currentRoute.value?.destination?.route != "welcome") {
                    BottomNavBar(navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "welcome",
                modifier = Modifier.padding(paddingValues)
            ) {
                // Core Screens in Bottom Navigation
                composable("home") { HomeScreen(navController) }
                composable("menu") { MenuOrderingPage(navController) }
                composable("events") { EventsScreen(navController) }
                composable("profile") { ProfileScreen(navController) }

                // Pass the dark mode state as a parameter to SettingsScreen
                composable("settings") {
                    SettingsScreen(navController, isDarkMode) { isDarkMode = it }
                }

                // New route for Language Selection
                composable("language_selection") { LanguageSelectionScreen(navController) }

                // Other Screens
                composable("welcome") {
                    WelcomeScreen(
                        onLoginClick = { navController.navigate("login") },
                        onSignUpClick = { navController.navigate("signUp") },
                        onHomeClick = { navController.navigate("home") }
                    )
                }
                composable("login") { LoginScreen(navController) }
                composable("signUp") { SignUpScreen(navController) }
                composable("checkout") { CheckoutScreen(navController) }
                composable("order_status") { OrderStatusScreen(navController) }

                // New Menu Item Info Page Route
                composable("menu_item_info/{menuItemId}") { backStackEntry ->
                    val menuItemId = backStackEntry.arguments?.getString("menuItemId")?.toIntOrNull()
                    val menuItem = getMenuItemById(menuItemId) // Fetch the menu item from your data source
                    menuItem?.let { MenuItemInfoPage(navController, it) }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Menu") },
            label = { Text("Menu") },
            selected = false,
            onClick = { navController.navigate("menu") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Event, contentDescription = "Events") },
            label = { Text("Events") },
            selected = false,
            onClick = { navController.navigate("events") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { navController.navigate("profile") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { navController.navigate("settings") }
        )
    }
}
