import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOrderingPage(navController: NavController, cartItems: MutableList<MenuItem>) {
    val db = FirebaseFirestore.getInstance()

    var menuItems = remember { mutableStateListOf<MenuItem>() }
    val searchQuery = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Define the desired order of categories (move this up here!)
    val categoryOrder = listOf(
        "Starters", "Mains", "Sides", "Lunch", "Desserts", "Bakery", "Drinks"
    )

    val filterOptions = listOf("All") + categoryOrder
    var selectedFilter by remember { mutableStateOf("All") }

    val dietaryOptions = listOf("Vegan", "Vegetarian", "Gluten-Free")
    var selectedDietaryFilter by remember { mutableStateOf<String?>(null) }

    var filterExpanded by remember { mutableStateOf(true) }

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
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IrishGreen,
                unfocusedBorderColor = LightGreen,
                cursorColor = IrishGreen
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = IrishGreen
            )

            IconButton(onClick = { filterExpanded = !filterExpanded }) {
                Icon(
                    imageVector = if (filterExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (filterExpanded) "Collapse Filters" else "Expand Filters",
                    tint = IrishGreen
                )
            }
        }

        AnimatedVisibility(
            visible = filterExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                // Category Filters
                Text(
                    text = "Filter by Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextGreen,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { option ->
                        val isSelected = selectedFilter == option
                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected) IrishGreen else LightGreen,
                            label = "categoryChipBackground"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            label = "categoryChipScale"
                        )
                        val elevation = if (isSelected) 6.dp else 2.dp

                        Surface(
                            color = backgroundColor,
                            shape = RoundedCornerShape(50),
                            shadowElevation = elevation,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable { selectedFilter = option }
                        ) {
                            Text(
                                text = option,
                                color = if (isSelected) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dietary Filters
                Text(
                    text = "Filter by Dietary Options",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextGreen,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dietaryOptions) { diet ->
                        val isSelected = selectedDietaryFilter == diet
                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected) IrishGreen else LightGreen,
                            label = "dietChipBackground"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            label = "dietChipScale"
                        )
                        val elevation = if (isSelected) 6.dp else 2.dp

                        Surface(
                            color = backgroundColor,
                            shape = RoundedCornerShape(50),
                            shadowElevation = elevation,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable {
                                    selectedDietaryFilter = if (isSelected) null else diet
                                }
                        ) {
                            Text(
                                text = diet,
                                color = if (isSelected) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        selectedFilter = "All"
                        selectedDietaryFilter = null
                        searchQuery.value = ""
                    }
                ) {
                    Text("Clear Filters", color = IrishGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredMenuItems = menuItems.filter {
            val matchesSearch = it.name.contains(searchQuery.value, ignoreCase = true) ||
                    it.description.contains(searchQuery.value, ignoreCase = true)
            val matchesFilter = selectedFilter == "All" || it.category == selectedFilter
            val matchesDietary = when (selectedDietaryFilter) {
                "Vegan" -> it.isVegan
                "Vegetarian" -> it.isVegetarian
                "Gluten-Free" -> it.isGlutenFree
                else -> true
            }
            matchesSearch && matchesFilter && matchesDietary
        }

        LazyColumn {
            // Group the items by category and order them based on the defined category order
            categoryOrder.forEach { category ->
                val itemsInCategory = filteredMenuItems.filter { it.category == category }
                if (itemsInCategory.isNotEmpty()) {
                    item {
                        Text(
                            text = category.uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = IrishGreen,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // If category is Drinks — split into subcategories using tags
                    if (category == "Drinks") {

                        // Define your Spirits subcategories in desired order
                        val spiritsOrder = listOf(
                            "Whiskey", "Poitín", "Brandy", "Gin", "Liqueurs", "Vodka"
                        )

                        val subcategoryGroups = itemsInCategory.groupBy { it.tags.firstOrNull() ?: "Other" }

                        // Separate spirits from other subcategories
                        val spiritSubcategories = spiritsOrder.filter { subcategoryGroups.containsKey(it) }
                        val otherSubcategories = subcategoryGroups.keys.filterNot { spiritsOrder.contains(it) }.sorted()

                        if (spiritSubcategories.isNotEmpty()) {
                            item {
                                Text(
                                    text = "SPIRITS",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IrishGreen,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }

                            spiritSubcategories.forEach { subCategory ->
                                val drinks = subcategoryGroups[subCategory] ?: emptyList()

                                item {
                                    Text(
                                        text = subCategory.uppercase(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextGreen,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    // Special description for Whiskey
                                    if (subCategory.equals("Whiskey", ignoreCase = true)) {
                                        Text(
                                            text = "Irish whiskey is a beloved and distinct whiskey category renowned for its smooth, approachable character. " +
                                                    "It's traditionally triple-distilled, often aged in various cask types, including ex-bourbon and sherry casks, " +
                                                    "resulting in a balanced and flavourful spirit. Jameson is a good place to start …",
                                            fontSize = 14.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }

                                items(drinks) { item ->
                                    MenuItemCard(
                                        item = item,
                                        cartItems = cartItems,
                                        snackbarHostState = snackbarHostState,
                                        coroutineScope = coroutineScope,
                                        navController = navController
                                    )
                                }
                            }
                        }

                        // Now display all non-spirit subcategories
                        otherSubcategories.forEach { subCategory ->
                            val drinks = subcategoryGroups[subCategory] ?: emptyList()

                            item {
                                Text(
                                    text = subCategory.uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextGreen,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            items(drinks) { item ->
                                MenuItemCard(
                                    item = item,
                                    cartItems = cartItems,
                                    snackbarHostState = snackbarHostState,
                                    coroutineScope = coroutineScope,
                                    navController = navController
                                )
                            }
                        }
                    } else {  // Default case for other categories
                        items(itemsInCategory) { item ->
                            MenuItemCard(
                                item = item,
                                cartItems = cartItems,
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }

        // Snackbar Host
        SnackbarHost(hostState = snackbarHostState)
    }
}