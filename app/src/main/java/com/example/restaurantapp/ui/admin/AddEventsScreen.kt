package com.example.restaurantapp.ui.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEventsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<Timestamp?>(null) }
    var imageUrl by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(true) }

    var showConfirmation by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val fullDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            selectedDateTime = Timestamp(calendar.time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            // once date is set, immediately show time picker
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // Minimum date = today
        datePicker.minDate = System.currentTimeMillis() - 1000
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Confirm Add") },
            text = { Text("Are you sure you want to save this event?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmation = false
                    saveEvent(
                        db, context, navController,
                        title, description, selectedDateTime, imageUrl,
                        tags, capacity, isFeatured, isPublic
                    ) {
                        title = ""
                        description = ""
                        selectedDateTime = null
                        imageUrl = ""
                        tags = ""
                        capacity = ""
                        isFeatured = false
                        isPublic = true
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) { Text("Cancel") }
            }
        )
    }

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

        Button(onClick = { datePickerDialog.show() }) {
            Text(
                text = if (selectedDateTime != null) {
                    "Selected: ${fullDateFormat.format(selectedDateTime!!.toDate())}"
                } else {
                    "Pick Event Date & Time"
                }
            )
        }

        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
        OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma-separated)") })

        OutlinedTextField(
            value = capacity,
            onValueChange = { capacity = it },
            label = { Text("Capacity (optional)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
            Text("Featured Event")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isPublic, onCheckedChange = { isPublic = it })
            Text("Public Event")
        }

        Button(
            onClick = {
                when {
                    title.isBlank() -> Toast.makeText(context, "Please enter a title.", Toast.LENGTH_SHORT).show()
                    description.isBlank() -> Toast.makeText(context, "Please enter a description.", Toast.LENGTH_SHORT).show()
                    selectedDateTime == null -> Toast.makeText(context, "Please pick a date and time.", Toast.LENGTH_SHORT).show()
                    imageUrl.isBlank() -> Toast.makeText(context, "Please provide an image URL.", Toast.LENGTH_SHORT).show()
                    tags.isBlank() -> Toast.makeText(context, "Please add at least one tag.", Toast.LENGTH_SHORT).show()
                    else -> showConfirmation = true
                }
            },
            enabled = !isSaving
        ) {
            Text(if (isSaving) "Saving..." else "Save Event")
        }
    }
}

private fun saveEvent(
    db: FirebaseFirestore,
    context: android.content.Context,
    navController: NavController,
    title: String,
    description: String,
    selectedDate: Timestamp?,
    imageUrl: String,
    tags: String,
    capacity: String,
    isFeatured: Boolean,
    isPublic: Boolean,
    onSuccess: () -> Unit
) {
    if (selectedDate == null) {
        Toast.makeText(context, "Date is not set.", Toast.LENGTH_LONG).show()
        return
    }

    // ⚠️ Check if the selected date is in the past
    if (selectedDate.toDate().before(Date())) {
        Toast.makeText(context, "Event date cannot be in the past.", Toast.LENGTH_LONG).show()
        return
    }

    val id = generateEventIdFromName(title)

    db.collection("events").document(id).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                Toast.makeText(context, "An event with this title already exists!", Toast.LENGTH_LONG).show()
            } else {
                val event = Event(
                    id = id,
                    title = title,
                    description = description,
                    date = selectedDate,
                    imageUrl = imageUrl,
                    tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    capacity = capacity.toIntOrNull(),
                    isFeatured = isFeatured,
                    isPublic = isPublic
                )

                db.collection("events")
                    .document(id)
                    .set(event)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Event added!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                        navController.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error checking duplicates: ${it.message}", Toast.LENGTH_LONG).show()
        }
}


fun generateEventIdFromName(name: String): String {
    val normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
        .replace("[^\\p{ASCII}]".toRegex(), "")
        .lowercase()
        .replace("\\s+".toRegex(), "_")
        .replace("[^a-z0-9_]".toRegex(), "")

    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "${normalized}_$timestamp"
}