package com.example.restaurantapp

import CartPage
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
import com.example.restaurantapp.ui.auth.LoginScreen
import com.example.restaurantapp.ui.auth.SignUpScreen
import com.example.restaurantapp.ui.auth.WelcomeScreen
import com.example.restaurantapp.ui.cart.CheckoutScreen
import com.example.restaurantapp.ui.cart.OrderStatusScreen
import com.example.restaurantapp.ui.home.EventsScreen
import com.example.restaurantapp.ui.home.HomeScreen
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.settings.LanguageSelectionScreen
import com.example.restaurantapp.ui.settings.ProfileScreen
import com.example.restaurantapp.ui.settings.SettingsScreen

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
    val cartItems = remember { mutableStateListOf<MenuItem>() } // Shared cart state

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
                composable("menu") { MenuOrderingPage(navController, cartItems) }
                composable("events") { EventsScreen(navController) }
                composable("profile") { ProfileScreen(navController) }
                composable("cart") { CartPage(navController, cartItems) } // Added Cart Page

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

            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home", // Highlight if it's the current route
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) // Clear backstack
                    launchSingleTop = true // Avoid multiple instances
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Menu") },
            label = { Text("Menu") },
            selected = currentRoute == "menu",
            onClick = {
                navController.navigate("menu") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            selected = currentRoute == "cart",
            onClick = {
                navController.navigate("cart") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Event, contentDescription = "Events") },
            label = { Text("Events") },
            selected = currentRoute == "events",
            onClick = {
                navController.navigate("events") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == "settings",
            onClick = {
                navController.navigate("settings") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    }
}

