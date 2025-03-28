import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.restaurantapp.ui.model.MenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MenuItemCard(
    item: MenuItem,
    cartItems: MutableList<MenuItem>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    var showDialog by remember { mutableStateOf(false) }
    var customization by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu Item Image - Using Coil
            val painter = rememberAsyncImagePainter(model = item.imageUrl)
            Image(
                painter = painter,
                contentDescription = item.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop // or ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Menu Item Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
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
                    color = Color(0xFF2E7D32) // IrishGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Add to Cart Button
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)) // GoldenYellow
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
