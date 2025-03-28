import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.restaurantapp.ui.model.MenuItem
import com.example.restaurantapp.ui.theme.*

@Composable
fun CartPage(navController: NavController, cartItems: MutableList<MenuItem>) {
    var total by remember { mutableDoubleStateOf(cartItems.sumOf { it.price }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IrishGreen,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Make the list scrollable while keeping checkout button visible
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn {
                items(cartItems) { item ->
                    CartItemCard(item, cartItems) {
                        total = cartItems.sumOf { it.price }
                    }
                }
            }
        }

        // Checkout Section - Fixed at Bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total: €${"%.2f".format(total)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IrishGreen
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("checkout") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow)
            ) {
                Text("Proceed to Checkout", color = Color.Black)
            }
        }
    }
}

@Composable
fun CartItemCard(item: MenuItem, cartItems: MutableList<MenuItem>, onUpdate: () -> Unit) {
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
            // Image Loading with Coil
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextBlack
                )
                // ✅ Display customization text
                if (item.description.contains("Custom:")) {
                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
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
                onClick = {
                    cartItems.remove(item)
                    onUpdate()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Remove", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
