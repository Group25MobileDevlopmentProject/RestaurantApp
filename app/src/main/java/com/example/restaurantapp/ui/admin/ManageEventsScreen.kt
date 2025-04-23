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
import com.example.restaurantapp.ui.model.Event
import com.example.restaurantapp.ui.theme.IrishGreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.restaurantapp.ui.components.EmptyStateScreen

@Composable
fun ManageEventsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }

    LaunchedEffect(Unit) {
        val snapshot = db.collection("events").get().await()
        events = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Event::class.java)?.copy(id = doc.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Manage Events",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = IrishGreen,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Button(
            onClick = { navController.navigate("admin_add_event") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IrishGreen)
        ) {
            Text("Add New Event", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (events.isEmpty()) {
            EmptyStateScreen(
                message = "No events available.",
                actionLabel = "Add Event"
            ) {
                navController.navigate("admin_add_event")
            }
        } else {
            LazyColumn {
                items(events.sortedBy { it.date }) { event ->
                    AdminEventCard(event, navController, db) {
                        events = events.filter { it.id != event.id }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminEventCard(
    event: Event,
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
                Text(text = event.title, style = MaterialTheme.typography.titleMedium)

                Text(
                    text = event.date.toDate().toString(),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${event.rsvpUserIds.size} attending",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = { navController.navigate("admin_edit_events/${event.id}") }
            ) {
                Text("Edit")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    db.collection("events").document(event.id).delete()
                    onDelete()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        }
    }
}