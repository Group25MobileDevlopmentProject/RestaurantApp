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
import com.example.restaurantapp.ui.admin.AddMenuItemScreen
import com.example.restaurantapp.ui.admin.AdminScreen
import com.example.restaurantapp.ui.admin.EditMenuItemScreen
import com.example.restaurantapp.ui.admin.ManageEventsScreen
import com.example.restaurantapp.ui.admin.ManageMenuItemsScreen
import com.example.restaurantapp.ui.admin.SendNotificationsScreen
import com.example.restaurantapp.ui.admin.ViewOrders
import com.example.restaurantapp.ui.auth.AuthScreen
import com.example.restaurantapp.ui.auth.WelcomeScreen
import com.example.restaurantapp.ui.cart.CheckoutScreen
import com.example.restaurantapp.ui.cart.OrderConfirmationScreen
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

    // --- Your role fetching logic ---
    val user = auth.currentUser
    var userRole by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user) {
        user?.let {
            val db = FirebaseFirestore.getInstance()
            val doc = db.collection("users").document(it.uid).get().await()
            userRole = doc.getString("role")
        }
    }
    // ---------------------------------

    RestaurantAppTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState()

        Scaffold(
            bottomBar = {
                val hideBottomNavRoutes = listOf("welcome", "auth")
                if (currentRoute.value?.destination?.route !in hideBottomNavRoutes) {
                    BottomNavBar(navController, userRole)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = if (authState.value != null) "home" else "welcome",
                modifier = Modifier.padding(paddingValues)
            ) {
                // Your navigation setup as it already is
                composable("home") { HomeScreen(navController) }
                composable("menu") { MenuOrderingPage(navController, cartItems) }
                composable("menu_item_info/{itemId}") { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                    val db = FirebaseFirestore.getInstance()

                    var menuItem by remember { mutableStateOf<MenuItem?>(null) }

                    LaunchedEffect(itemId) {
                        val doc = db.collection("menuItems").document(itemId).get().await()
                        menuItem = doc.toObject(MenuItem::class.java)?.copy(
                            id = doc.id,
                            isVegan = doc.getBoolean("isVegan") == true,
                            isVegetarian = doc.getBoolean("isVegetarian") == true,
                            isGlutenFree = doc.getBoolean("isGlutenFree") == true
                        )
                    }

                    menuItem?.let {
                        MenuItemInfoPage(navController, it)
                    } ?: Text("Loading...")
                }
                composable("events") { EventsScreen(navController = navController, contentPadding = paddingValues) }
                composable("profile") { ProfileScreen(navController, authState.value) }

                // Admin routes
                composable("admin") { AdminScreen(navController) }
                composable("admin_manage_menu") { ManageMenuItemsScreen(navController) }
                composable("admin_add_menu_item") { AddMenuItemScreen(navController) }
                composable("admin_edit_menu_item/{itemId}") { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                    EditMenuItemScreen(navController, itemId)
                }
                composable("admin_manage_events") { ManageEventsScreen(navController) }
                composable("admin_send_notifications") { SendNotificationsScreen(navController) }
                composable("admin_view_orders") { ViewOrders(navController) }

                // Settings routes
                composable("settings") { SettingsScreen(navController, isDarkMode) { isDarkMode = it } }
                composable("language_selection") { LanguageSelectionScreen(navController) }

                // Authentication routes
                composable("welcome") {
                    WelcomeScreen(
                        onAuthClick = { navController.navigate("auth") },
                        onHomeClick = { navController.navigate("home") }
                    )
                }
                composable("auth") { AuthScreen(navController) }

                // Cart routes
                composable("checkout") { CheckoutScreen(navController, cartItems) }
                composable("order_confirmation") { OrderConfirmationScreen(navController) }
                composable("cart") { CartPage(navController, cartItems) }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController, userRole: String?) {
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

        if (userRole == "admin") {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin") },
                label = { Text("Admin") },
                selected = currentRoute == "admin",
                onClick = {
                    navController.navigate("admin") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
