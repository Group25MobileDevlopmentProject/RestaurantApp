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
import coil.compose.rememberAsyncImagePainter
import com.example.restaurantapp.ui.model.Event
import com.example.restaurantapp.ui.theme.IrishGreen
import com.example.restaurantapp.ui.theme.LightGreen
import com.example.restaurantapp.ui.theme.TextGreen
import com.example.restaurantapp.util.formatTimestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.platform.LocalContext
import com.example.restaurantapp.util.NotificationScheduler

@Composable
fun EventsScreen(contentPadding: PaddingValues = PaddingValues()) {
    val events = remember { mutableStateListOf<Event>() }
    val db = FirebaseFirestore.getInstance()
    var searchQuery by remember { mutableStateOf("") }
    var noUpcomingEvents by remember { mutableStateOf(false) }

    // State for handling the collapsible past events section
    var expandedPastEvents by remember { mutableStateOf(false) }

    // Access context from Composable
    val context = LocalContext.current // Get the context from the composable

    DisposableEffect(Unit) {
        val listener = db.collection("events")
            .orderBy("date")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("EventsScreen", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    events.clear()
                    noUpcomingEvents = snapshots.isEmpty
                    for (doc in snapshots) {
                        val event = doc.toObject(Event::class.java)
                        events.add(event)

                        // Trigger notification 24 hours before the event starts
                        val currentTimeMillis = System.currentTimeMillis()
                        val eventTimeMillis = event.date.toDate().time
                        val delayInMillis = eventTimeMillis - currentTimeMillis - 86400000L // 1 day before event

                        if (delayInMillis > 0) {
                            // Schedule the notification
                            NotificationScheduler.scheduleEventNotification(context, event.title, event.description, delayInMillis)
                        }
                    }
                }
            }

        onDispose { listener.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Events",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = IrishGreen,
                modifier = Modifier.padding(bottom = 12.dp) // Match HomeScreen bottom padding
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

        // Check if there are no upcoming events
        if (noUpcomingEvents) {
            Text(
                text = "No upcoming events.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Filter events based on search query (title, description, and tags)
                val filteredEvents = events.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true) ||
                            it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) } // Check if any tag matches
                }

                // Upcoming Events
                items(filteredEvents.filter { it.status != "past" }) { event ->
                    EventCard(event = event, events = events) // Pass events list to EventCard
                }

                // Past Events Collapsible Section
                item {
                    if (filteredEvents.any { it.status == "past" }) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedPastEvents = !expandedPastEvents }
                                    .padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (expandedPastEvents) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Past Events",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            AnimatedVisibility(visible = expandedPastEvents) {
                                Column(
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                ) {
                                    filteredEvents.filter { it.status == "past" }.forEachIndexed { index, event ->
                                        EventCard(event = event, events = events) // Display Past Event

                                        // Add extra space between past events
                                        if (index < filteredEvents.filter { it.status == "past" }.size - 1) {
                                            Spacer(modifier = Modifier.height(24.dp)) // Adjust this height for more space
                                        }
                                    }
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
fun EventCard(event: Event, events: MutableList<Event>) {
    val user = FirebaseAuth.getInstance().currentUser
    val hasRSVPed = remember(user, event.rsvpUserIds) { user?.uid in event.rsvpUserIds }

    val daysAgo = remember(event.createdAt) {
        val now = System.currentTimeMillis()
        val createdAtMillis = event.createdAt.toDate().time
        val diffMillis = now - createdAtMillis
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        days
    }

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatTimestamp(event.date),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    if (event.status == "past") {
                        Text(
                            text = "Past Event",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (event.isFeatured) {
                        Text(
                            text = "★ Featured",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Added $daysAgo day${if (daysAgo != 1) "s" else ""} ago",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                // Make tags horizontally scrollable
                if (event.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState()) // Enable horizontal scrolling
                    ) {
                        event.tags.forEach {
                            AssistChip(onClick = {}, label = { Text(it) })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(event.description)

                //TestNotificationButton()


                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${event.rsvpUserIds.size} attending",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Button(
                        onClick = {
                            val db = FirebaseFirestore.getInstance()
                            val eventRef = db.collection("events").document(event.id)

                            if (user != null) {
                                val update = if (hasRSVPed) {
                                    mapOf("rsvpUserIds" to FieldValue.arrayRemove(user.uid))
                                } else {
                                    mapOf("rsvpUserIds" to FieldValue.arrayUnion(user.uid))
                                }
                                eventRef.update(update).addOnSuccessListener {
                                    // Manually update the event in the events list after the update
                                    val updatedEvent = event.copy(
                                        rsvpUserIds = if (hasRSVPed) event.rsvpUserIds - user.uid else event.rsvpUserIds + user.uid
                                    )
                                    val index = events.indexOfFirst { it.id == event.id }
                                    if (index != -1) {
                                        events[index] = updatedEvent
                                    }
                                }
                            } else {
                                // Show login warning
                                Log.d("RSVP", "User not logged in.")
                            }
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(if (hasRSVPed) "Cancel RSVP" else "RSVP")
                    }
                }
            }
        }
    }
}

@Composable
fun TestNotificationButton() {
    val context = LocalContext.current

    Button(onClick = {
        // Trigger the notification manually
        val delayInMillis = 5000L // 5 seconds for testing

        Log.d("NotificationTest", "Scheduling test notification, Delay: $delayInMillis ms")

        // Schedule a simple notification
        NotificationScheduler.scheduleEventNotification(
            context,
            "Test Notification", // Title for the notification
            "This is a test notification message.", // Message for the notification
            delayInMillis
        )
    }) {
        Text("Test Notification")
    }
}
