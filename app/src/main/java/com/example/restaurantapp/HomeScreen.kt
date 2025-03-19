package com.example.restaurantapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.theme.*

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val featuredItems = remember {
        listOf(
            Pair("Try our Guinness Beef Stew!", R.drawable.guiness_beef_stew),
            Pair("Enjoy our Irish Coffee!", R.drawable.irish_coffee),
            Pair("Taste our Shepherd's Pie!", R.drawable.shepherds_pie)
        )
    }
    val events = remember { listOf("Live Music on Friday", "Happy Hour on Saturday", "St. Patrick's Day Special") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGreen)
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to Our Pub!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Carousel of featured items with images
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(featuredItems) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = IrishGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .width(200.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = item.second),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text(text = item.first, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick access buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("menu") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IrishGreen)
            ) {
                Text(text = "Order Food", color = Color.White)
            }
            Button(
                onClick = { /* Navigate to View Menu */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
            ) {
                Text(text = "View Menu", color = Color.White)
            }
            Button(
                onClick = { /* Navigate to Check Order Status */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text(text = "Order Status", color = Color.White)
            }
        }

        // Section for upcoming events and special offers
        Text(
            text = "Upcoming Events & Special Offers",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            events.forEach { event ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = event, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Button(
            onClick = { /* Navigate to Events & Special Offers page */ },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IrishGreen),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "See All Events & Offers", color = Color.White)
        }
    }
}