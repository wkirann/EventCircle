package com.example.eventcircle.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.eventcircle.data.model.Event
import com.example.eventcircle.viewmodel.EventViewModel
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import java.util.*

@Composable
fun AddEventScreen(
    viewModel: EventViewModel,
    onEventAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Tech", "Sports", "Cultural")
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var date by remember { mutableStateOf(calendar.timeInMillis) }
    val scrollState = rememberScrollState()

    // Date and Time Picker Dialogs
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    date = calendar.timeInMillis
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp)) // Added to push heading lower
        Text(
            text = "ADD NEW EVENT",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Event Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedCategory.ifEmpty { "Select Category" })
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { datePickerDialog.show() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Date and Time")
        }

        Button(
            onClick = {
                if (title.isNotBlank() && location.isNotBlank() && description.isNotBlank() && selectedCategory.isNotBlank()) {
                    viewModel.insert(
                        Event(
                            title = title,
                            date = date,
                            location = location,
                            description = description,
                            category = selectedCategory
                        )
                    )
                    Toast.makeText(context, "Event Added Successfully!", Toast.LENGTH_SHORT).show()
                    onEventAdded()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && location.isNotBlank() && description.isNotBlank() && selectedCategory.isNotBlank()
        ) {
            Text("Save Event")
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the back button to the bottom
        IconButton(
            onClick = onEventAdded,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}