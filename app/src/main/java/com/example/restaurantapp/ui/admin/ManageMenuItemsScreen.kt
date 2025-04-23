package com.example.restaurantapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.theme.IrishGreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.restaurantapp.ui.components.EmptyStateScreen

@Composable
fun ManageMenuItemsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var menuItems by remember { mutableStateOf<List<MenuItem>>(emptyList()) }

    val categoryOrder = listOf(
        "Starters", "Mains", "Sides", "Lunch", "Desserts", "Bakery", "Drinks"
    )

    LaunchedEffect(Unit) {
        val snapshot = db.collection("menuItems").get().await()
        menuItems = snapshot.documents.mapNotNull { doc ->
            doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Manage Menu Items",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IrishGreen,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Button(
            onClick = { navController.navigate("admin_add_menu_item") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IrishGreen)
        ) {
            Text("Add New Item", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (menuItems.isEmpty()) {
            EmptyStateScreen(
                message = "No menu items available.",
                actionLabel = "Add Menu Item"
            ) {
                navController.navigate("admin_add_menu_item")
            }
        } else {
            LazyColumn {
                categoryOrder.forEach { category ->
                    val itemsInCategory = menuItems.filter { it.category == category }
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

                        if (category == "Drinks") {
                            val subcategoryGroups = itemsInCategory.groupBy { it.tags.firstOrNull() ?: "Other" }

                            subcategoryGroups.forEach { (subCategory, drinks) ->
                                item {
                                    Text(
                                        text = subCategory.uppercase(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                items(drinks) { item ->
                                    AdminMenuItemCard(item, navController, db) {
                                        menuItems = menuItems.filter { it.id != item.id }
                                    }
                                }
                            }
                        } else {
                            items(itemsInCategory) { item ->
                                AdminMenuItemCard(item, navController, db) {
                                    menuItems = menuItems.filter { it.id != item.id }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMenuItemCard(
    item: MenuItem,
    navController: NavController,
    db: FirebaseFirestore,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "€${item.price}", style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { navController.navigate("admin_edit_menu_item/${item.id}") }
            ) {
                Text("Edit")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    db.collection("menuItems").document(item.id).delete()
                    onDelete()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        }
    }
}