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
import com.example.restaurantapp.ui.auth.AuthScreen
import com.example.restaurantapp.ui.auth.WelcomeScreen
import com.example.restaurantapp.ui.cart.CheckoutScreen
import com.example.restaurantapp.ui.cart.OrderStatusScreen
import com.example.restaurantapp.ui.home.EventsScreen
import com.example.restaurantapp.ui.home.HomeScreen
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.settings.LanguageSelectionScreen
import com.example.restaurantapp.ui.settings.ProfileScreen
import com.example.restaurantapp.ui.settings.SettingsScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RestaurantApp)
        setContent { AppContent() }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(): State<FirebaseUser?> {
    val auth = remember { Firebase.auth }
    val currentUser = remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { authState ->
            currentUser.value = authState.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }
    return currentUser
}

@Composable
fun AppContent() {
    var isDarkMode by remember { mutableStateOf(false) }
    val cartItems = remember { mutableStateListOf<MenuItem>() }
    val authState = rememberFirebaseAuthLauncher()
    val auth: FirebaseAuth = remember { Firebase.auth }

    RestaurantAppTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState()

        Scaffold(
            bottomBar = {
                val hideBottomNavRoutes = listOf("welcome", "auth")
                if (currentRoute.value?.destination?.route !in hideBottomNavRoutes) {
                    BottomNavBar(navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = if (authState.value != null) "home" else "welcome",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") { HomeScreen(navController) }
                composable("menu") { MenuOrderingPage(navController, cartItems) }
                composable("events") { EventsScreen(navController) }
                composable("profile") { ProfileScreen(navController, authState.value) }
                composable("cart") { CartPage(navController, cartItems) }
                composable("settings") { SettingsScreen(navController, isDarkMode) { isDarkMode = it } }
                composable("language_selection") { LanguageSelectionScreen(navController) }
                composable("welcome") { WelcomeScreen(
                    onAuthClick = { navController.navigate("auth") },
                    onHomeClick = { navController.navigate("home") }
                ) }
                composable("auth") { AuthScreen(navController) }
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
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
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
