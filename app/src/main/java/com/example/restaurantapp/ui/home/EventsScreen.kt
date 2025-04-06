package com.example.restaurantapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.restaurantapp.ui.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventsScreen(navController: NavController) {
    val events = remember { mutableStateListOf<Event>() }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("events")
            .orderBy("date")
            .get()
            .addOnSuccessListener { result ->
                events.clear()
                for (doc in result) {
                    doc.toObject(Event::class.java)?.let { events.add(it) }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Upcoming Events",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(events) { event ->
                EventCard(event)
            }
        }
    }
}


@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
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