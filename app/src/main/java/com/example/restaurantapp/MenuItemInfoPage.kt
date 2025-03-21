import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.MenuItem
import com.example.restaurantapp.ui.theme.DarkGreen
import com.example.restaurantapp.ui.theme.IrishGreen
import com.example.restaurantapp.ui.theme.TextGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemInfoPage(navController: NavController, menuItem: MenuItem) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopAppBar(
            title = { Text(menuItem.name, color = Color.White, fontSize = 24.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = menuItem.imageResId),
            contentDescription = menuItem.name,
            modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally)
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Price: €${menuItem.price}",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = IrishGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Vegetarian: ${if (menuItem.isVegetarian) "Yes" else "No"}",
            fontSize = 16.sp,
            color = TextGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Gluten-Free: ${if (menuItem.isGlutenFree) "Yes" else "No"}",
            fontSize = 16.sp,
            color = TextGreen
        )
    }
}
