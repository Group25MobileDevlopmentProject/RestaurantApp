import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOrderingPage(navController: NavController, cartItems: MutableList<MenuItem>) {
    val db = FirebaseFirestore.getInstance()

    var menuItems = remember { mutableStateListOf<MenuItem>() }

    val searchQuery = remember { mutableStateOf("") }

    // Snackbar for feedback when adding items
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Fetch the menu items from Firestore when the composable is first launched
    LaunchedEffect(key1 = true) {
        try {
            val snapshot = db.collection("menuItems").get().await()
            val items = snapshot.documents.mapNotNull { document ->
                document.toObject(MenuItem::class.java)?.copy(
                    id = document.id,
                    isVegan = document.getBoolean("isVegan") == true,
                    isVegetarian = document.getBoolean("isVegetarian") == true,
                    isGlutenFree = document.getBoolean("isGlutenFree") == true
                )
            }
            menuItems.clear()
            menuItems.addAll(items)
        } catch (e: Exception) {
            println("Error getting menu items: $e")
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        // Top Bar with Cart Icon
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Menu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = IrishGreen
            )
            IconButton(onClick = { navController.navigate("cart") }) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Cart",
                        tint = IrishGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    if (cartItems.isNotEmpty()) {
                        Badge(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            modifier = Modifier.padding(start = 16.dp, top = 0.dp)
                        ) {
                            Text("${cartItems.size}")
                        }
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search", color = TextGreen, fontSize = 16.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), // Optional: spacing above/below
            shape = MaterialTheme.shapes.medium, // Correctly applies rounded shape
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IrishGreen,
                unfocusedBorderColor = LightGreen,
                cursorColor = IrishGreen
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtered Menu Items
        val filteredMenuItems = menuItems.filter {
            it.name.contains(searchQuery.value, ignoreCase = true) ||
                    it.description.contains(searchQuery.value, ignoreCase = true)
        }

        LazyColumn {
            filteredMenuItems.groupBy { it.category }.forEach { (category, items) ->
            item {
                    Text(
                        text = category.uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IrishGreen,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(items) { item ->
                    MenuItemCard(
                        item = item,
                        cartItems = cartItems,
                        snackbarHostState = snackbarHostState,
                        coroutineScope = coroutineScope,
                        navController = navController // Pass NavController here
                    )
                }
            }
        }

        // Snackbar Host
        SnackbarHost(hostState = snackbarHostState)
    }
}