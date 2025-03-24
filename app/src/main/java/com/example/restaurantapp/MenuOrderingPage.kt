import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.MenuItem
import com.example.restaurantapp.R
import com.example.restaurantapp.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOrderingPage(navController: NavController, cartItems: MutableList<MenuItem>) {
    val menuItems = listOf(
        MenuItem(1, "Starters", "Bruschetta", 5.99, "Grilled bread with tomatoes", R.drawable.bruschetta, true, true, true),
        MenuItem(2, "Mains", "Steak", 19.99, "Grilled steak with fries", R.drawable.steak, false, false, false),
        MenuItem(3, "Drinks", "Lemonade", 2.99, "Freshly squeezed lemonade", R.drawable.lemonade, true, true, true),
        MenuItem(4, "Desserts", "Cheesecake", 6.99, "Creamy cheesecake with berries", R.drawable.cheesecake, true, false, false),
        MenuItem(5, "Specials", "Guinness Beef Stew", 14.99, "Beef stew with Guinness", R.drawable.guiness_beef_stew, false, false, false)
    )
    val searchQuery = remember { mutableStateOf("") }

    // Snackbar for feedback when adding items
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = "Cart",
                    tint = IrishGreen,
                    modifier = Modifier.size(28.dp)
                )
                if (cartItems.isNotEmpty()) {
                    Text("${cartItems.size}", color = Color.Red, fontSize = 12.sp)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search", color = TextGreen, fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IrishGreen,
                unfocusedBorderColor = LightGreen,
                cursorColor = IrishGreen
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtered Menu Items
        val filteredMenuItems = menuItems.filter { it.name.contains(searchQuery.value, ignoreCase = true) }

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
                    MenuItemCard(item, cartItems, snackbarHostState, coroutineScope)
                }
            }
        }

        // Snackbar Host
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun MenuItemCard(item: MenuItem, cartItems: MutableList<MenuItem>, snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
    var showDialog by remember { mutableStateOf(false) }
    var customization by remember { mutableStateOf("") } // Stores customizations

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu Item Image
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.name,
                modifier = Modifier.size(72.dp).clip(MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Menu Item Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextBlack
                )
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "€${item.price}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = IrishGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Add to Cart Button (Opens Customization Dialog)
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow)
            ) {
                Text("Add", color = Color.Black, fontSize = 14.sp)
            }
        }
    }

    // Customization Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Customize ${item.name}") },
            text = {
                Column {
                    OutlinedTextField(
                        value = customization,
                        onValueChange = { customization = it },
                        label = { Text("Add special requests (e.g., no onions, extra cheese)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        cartItems.add(item.copy(description = "${item.description} (Custom: $customization)"))
                        showDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("${item.name} added to cart with customization!")
                        }
                    }
                ) {
                    Text("Add to Cart")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
