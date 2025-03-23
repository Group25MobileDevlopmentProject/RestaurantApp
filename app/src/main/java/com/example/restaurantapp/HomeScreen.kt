package com.example.restaurantapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val featuredItems = remember {
        listOf(
            Triple("Guinness Beef Stew", R.drawable.guiness_beef_stew, "Slow-cooked beef in Guinness sauce."),
            Triple("Irish Coffee", R.drawable.irish_coffee, "Strong coffee with Irish whiskey and cream."),
            Triple("Shepherd's Pie", R.drawable.shepherds_pie, "Classic minced lamb with mashed potatoes.")
        )
    }
    val events = remember { listOf("Live Music Friday - 8 PM", "Happy Hour Saturday - 6-9 PM", "St. Patrick's Day Special - March 17") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF1E293B))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.irish_pub),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome to Our Pub!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Featured Items
        Text("Featured Dishes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
        LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
            items(featuredItems) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(8.dp).width(250.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = item.second),
                            contentDescription = item.first,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp))
                        )
                        Text(text = item.first, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        Text(text = item.third, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // Upcoming Events
        Text("Upcoming Events", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
        Column {
            events.forEach { event ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = event, fontSize = 16.sp)
                    }
                }
            }
        }

        // Special Offers
        Text("Special Offers", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
        Column {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.elevatedCardElevation(6.dp)
            ) {
                Text(text = "50% Off on all Irish drinks this weekend!", fontSize = 16.sp, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

// Function to fetch menu item by ID
fun getMenuItemById(id: Int?): MenuItem? {
    val menuItems = listOf(
        MenuItem(1, "Starters", "Bruschetta", 5.99, "Grilled bread with tomatoes", R.drawable.bruschetta, true, true),
        MenuItem(2, "Mains", "Steak", 19.99, "Grilled steak with fries", R.drawable.steak, false, false),
        MenuItem(3, "Drinks", "Lemonade", 2.99, "Freshly squeezed lemonade", R.drawable.lemonade, true, true),
        MenuItem(4, "Desserts", "Cheesecake", 6.99, "Creamy cheesecake with berries", R.drawable.cheesecake, true, false),
        MenuItem(5, "Specials", "Guinness Beef Stew", 14.99, "Beef stew with Guinness", R.drawable.guiness_beef_stew, false, false)
    )
    return menuItems.find { it.id == id }
}