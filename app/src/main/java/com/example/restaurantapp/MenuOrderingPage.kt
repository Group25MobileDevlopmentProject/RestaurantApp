package com.example.restaurantapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.theme.DarkGreen

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
    val vegetarianFilter = remember { mutableStateOf(false) }
    val glutenFreeFilter = remember { mutableStateOf(false) }
    val categories = menuItems.map { it.category }.distinct()
    val selectedCategory = remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopAppBar(
            title = { Text("Menu Ordering", color = Color.White, fontSize = 24.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Category filter drop-down menu
        Box {
            Button(onClick = { expanded = true }) {
                Text("Filters")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        selectedCategory.value = "All"
                        expanded = false
                    }
                )
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory.value = category
                            expanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = vegetarianFilter.value,
                                onCheckedChange = { vegetarianFilter.value = it }
                            )
                            Text("Vegetarian")
                        }
                    },
                    onClick = { vegetarianFilter.value = !vegetarianFilter.value }
                )
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = glutenFreeFilter.value,
                                onCheckedChange = { glutenFreeFilter.value = it }
                            )
                            Text("Gluten-Free")
                        }
                    },
                    onClick = { glutenFreeFilter.value = !glutenFreeFilter.value }
                )
                // Add more filters as needed
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Group menu items by category
        val filteredMenuItems = menuItems.filter {
            (selectedCategory.value == "All" || it.category == selectedCategory.value) &&
                    it.name.contains(searchQuery.value, ignoreCase = true) &&
                    (!vegetarianFilter.value || it.isVegetarian) &&
                    (!glutenFreeFilter.value || it.isGlutenFree)
        }

        // Display each category in a separate section
        LazyColumn {
            filteredMenuItems.groupBy { it.category }.forEach { (category, items) ->
                item {
                    Text(
                        text = category,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(items) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Image(
                                painter = painterResource(id = item.imageResId),
                                contentDescription = item.name,
                                modifier = Modifier.size(64.dp).padding(end = 16.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(item.description)
                                Text("Price: \$${item.price}", fontWeight = FontWeight.Bold)
                            }
                            Button(onClick = { /* Add to cart */ }) {
                                Text("Add to Cart")
                            }
                        }
                    }
                }
            }
        }
    }
}