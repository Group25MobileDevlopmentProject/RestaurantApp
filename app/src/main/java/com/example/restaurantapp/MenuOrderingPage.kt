import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.restaurantapp.MenuItem
import com.example.restaurantapp.R
import com.example.restaurantapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOrderingPage(navController: NavController) {
    val menuItems = remember {
        listOf(
            MenuItem(1, "Starters", "Bruschetta", 5.99, "Grilled bread with tomatoes", R.drawable.bruschetta, true, true),
            MenuItem(2, "Mains", "Steak", 19.99, "Grilled steak with fries", R.drawable.steak, false, false),
            MenuItem(3, "Drinks", "Lemonade", 2.99, "Freshly squeezed lemonade", R.drawable.lemonade, true, true),
            MenuItem(4, "Desserts", "Cheesecake", 6.99, "Creamy cheesecake with berries", R.drawable.cheesecake, true, false),
            MenuItem(5, "Specials", "Guinness Beef Stew", 14.99, "Beef stew with Guinness", R.drawable.guiness_beef_stew, false, false)
        )
    }
    val searchQuery = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search", color = TextGreen, fontSize = 16.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
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
                    MenuItemCard(item, navController)
                }
            }
        }
    }

    // Bottom Navigation Bar
    BottomNavBar(navController)
}

@Composable
fun MenuItemCard(item: MenuItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { navController.navigate("menuItemInfo/${item.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(12.dp))
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "€${item.price}",
                    fontWeight = FontWeight.Bold,
                    color = IrishGreen,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { /* Add to cart functionality */ },
                colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow)
            ) {
                Text("Add", color = Color.Black, fontSize = 14.sp)
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
            selected = true,
            onClick = { navController.navigate("menu") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Event, contentDescription = "Events") },
            label = { Text("Events") },
            selected = false,
            onClick = { navController.navigate("events") }
        )
    }
}
