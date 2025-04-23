package com.example.restaurantapp.ui.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditEventsScreen(
    navController: NavController,
    eventId: String
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) }

    var isLoading by remember { mutableStateOf(true) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<Timestamp?>(null) }
    var imageUrl by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(true) }

    val calendar = remember { Calendar.getInstance() }

    // Fetch event from Firestore on first composition
    LaunchedEffect(eventId) {
        db.collection("events").document(eventId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    title = document.getString("title") ?: ""
                    description = document.getString("description") ?: ""
                    val timestamp = document.getTimestamp("date")
                    selectedDateTime = timestamp
                    imageUrl = document.getString("imageUrl") ?: ""
                    isFeatured = document.getBoolean("isFeatured") == true
                    tags = (document.get("tags") as? List<*>)?.joinToString(", ") ?: ""
                    isPublic = document.getBoolean("isPublic") == true
                }
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error loading event: ${it.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

            // Date picker for selecting date
            Button(onClick = {
                val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        selectedDateTime = Timestamp(calendar.time)
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
                    timePickerDialog.show()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

                datePickerDialog.show()
            }) {
                Text(
                    text = if (selectedDateTime != null) {
                        "Selected: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(selectedDateTime!!.toDate())}"
                    } else {
                        "Pick Event Date & Time"
                    }
                )
            }

            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
            OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma-separated)") })

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                Text("Featured Event")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {  // Public event checkbox
                Checkbox(checked = isPublic, onCheckedChange = { isPublic = it })
                Text("Public Event")
            }

            Button(onClick = {
                if (title.isNotBlank() && selectedDateTime != null) {
                    try {
                        db.collection("events").document(eventId).get().addOnSuccessListener { document ->
                            val existingRsvps = document?.get("rsvpUserIds") as? List<*> ?: emptyList<String>()

                            val updatedEvent = hashMapOf(
                                "title" to title,
                                "description" to description,
                                "date" to selectedDateTime,
                                "imageUrl" to imageUrl,
                                "tags" to tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                "isFeatured" to isFeatured,
                                "isPublic" to isPublic,  // Save public status
                                "rsvpUserIds" to existingRsvps  // Preserve RSVP data
                            )

                            db.collection("events").document(eventId)
                                .set(updatedEvent)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Event updated!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Invalid date format.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill in the title and date.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save Changes")
            }
        }
    }
}