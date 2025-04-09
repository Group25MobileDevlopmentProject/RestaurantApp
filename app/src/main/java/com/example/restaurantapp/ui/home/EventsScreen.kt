package com.example.restaurantapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.restaurantapp.ui.model.Event
import com.example.restaurantapp.ui.theme.IrishGreen
import com.example.restaurantapp.ui.theme.LightGreen
import com.example.restaurantapp.ui.theme.TextGreen
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventsScreen(navController: NavController, contentPadding: PaddingValues = PaddingValues()) {
    val events = remember { mutableStateListOf<Event>() }
    val db = FirebaseFirestore.getInstance()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("events")
            .orderBy("date")
            .get()
            .addOnSuccessListener { result ->
                events.clear()
                for (doc in result) {
                    doc.toObject(Event::class.java).let { events.add(it) }
                }
            }
    }

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Events",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = IrishGreen
//            )
//        }
//        // Search Bar
//        OutlinedTextField(
//            value = searchQuery,
//            onValueChange = { searchQuery = it },
//            label = { Text("Search Events", color = TextGreen, fontSize = 16.sp) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            shape = MaterialTheme.shapes.medium,
//            singleLine = true,
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = IrishGreen,
//                unfocusedBorderColor = LightGreen,
//                cursorColor = IrishGreen
//            )
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))

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
                    text = "Events",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = IrishGreen
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Events", color = TextGreen, fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Optional: spacing above/below
                shape = MaterialTheme.shapes.medium, // Correctly applies rounded shape
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IrishGreen,
                    unfocusedBorderColor = LightGreen,
                    cursorColor = IrishGreen
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

//        Text(
//            text = "Upcoming Events",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            color = IrishGreen,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val filteredEvents = events.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }

            items(filteredEvents) { event ->
                EventCard(event)
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(event.imageUrl),
                contentDescription = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (event.isFeatured) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "★ Featured",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(formatTimestamp(event.date), style = MaterialTheme.typography.labelMedium)

                if (event.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        event.tags.forEach {
                            AssistChip(onClick = {}, label = { Text(it) })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(event.description)
            }
        }
    }
}

fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    return try {
        val date = timestamp.toDate()
        SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        "Invalid date"
    }
}