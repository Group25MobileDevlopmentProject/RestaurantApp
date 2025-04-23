import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.theme.DarkGreen
import com.example.restaurantapp.ui.theme.IrishGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemInfoPage(
    navController: NavController,
    menuItem: MenuItem,
    cartItems: MutableList<MenuItem>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    var showDialog by remember { mutableStateOf(false) }
    var customization by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(menuItem.name, color = Color.White, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        },
        bottomBar = {
            Surface(
                color = IrishGreen,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "€${menuItem.price}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)) // GoldenYellow
                    ) {
                        Text("Add to Cart", color = Color.Black)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            val painter = rememberAsyncImagePainter(model = menuItem.imageUrl)
            Image(
                painter = painter,
                contentDescription = menuItem.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                menuItem.name,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(menuItem.description, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (menuItem.isVegetarian) AssistChip(
                    onClick = {},
                    label = { Text("Vegetarian") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = IrishGreen.copy(alpha = 0.2f))
                )
                if (menuItem.isVegan) AssistChip(
                    onClick = {},
                    label = { Text("Vegan") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = IrishGreen.copy(alpha = 0.2f))
                )
                if (menuItem.isGlutenFree) AssistChip(
                    onClick = {},
                    label = { Text("Gluten-Free") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = IrishGreen.copy(alpha = 0.2f))
                )
            }
        }
    }

    // Customization Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Customize ${menuItem.name}") },
            text = {
                OutlinedTextField(
                    value = customization,
                    onValueChange = { customization = it },
                    label = { Text("Special requests (e.g., no onions, extra cheese)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        cartItems.add(
                            menuItem.copy(description = "${menuItem.description} (Custom: $customization)")
                        )
                        showDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("${menuItem.name} added to cart with customization!")
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