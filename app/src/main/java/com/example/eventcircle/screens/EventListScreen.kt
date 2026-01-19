package com.example.eventcircle.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcircle.data.model.Event
import com.example.eventcircle.utils.formatDate
import com.example.eventcircle.viewmodel.EventViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@Composable
fun EventListScreen(
    viewModel: EventViewModel,
    onAddEventClick: () -> Unit,
    onParticipateClick: (Event) -> Unit,
    onViewClick: (Event) -> Unit,
    onRegisteredEventsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Upcoming") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var searchLocation by remember { mutableStateOf("") } // User-entered location
    var filteredByLocation by remember { mutableStateOf(false) } // Track if location filter is applied
    val filters = listOf("Upcoming", "Completed")
    val categories = listOf("All", "Tech", "Sports", "Cultural")
    val currentTime = System.currentTimeMillis()

    val events by if (selectedCategory == "All") {
        viewModel.allEvents
    } else {
        viewModel.getEventsByCategory(selectedCategory)
    }.collectAsState(initial = emptyList())

    // Filter events by time and location
    val filteredEvents = events.filter { event ->
        val matchesTime = when (selectedFilter) {
            "Upcoming" -> event.date >= currentTime
            "Completed" -> event.date < currentTime
            else -> true
        }
        val matchesLocation = if (filteredByLocation && searchLocation.isNotBlank()) {
            event.location.contains(searchLocation, ignoreCase = true)
        } else {
            true
        }
        matchesTime && matchesLocation
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEventClick) {
                Text("+")
            }
        },
        bottomBar = {
            Button(
                onClick = onRegisteredEventsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("VIEW REGISTERED EVENTS") // Updated to uppercase
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EVENTS",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Search bar for location
            OutlinedTextField(
                value = searchLocation,
                onValueChange = { searchLocation = it },
                label = { Text("Enter Your Location (e.g., Bangalore)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Search Nearby button
            Button(
                onClick = { filteredByLocation = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = searchLocation.isNotBlank()
            ) {
                Text("Search Nearby Events")
            }

            // Reset filter button
            if (filteredByLocation) {
                OutlinedButton(
                    onClick = {
                        filteredByLocation = false
                        searchLocation = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset Location Filter")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Filter: $selectedFilter")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filters.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    selectedFilter = filter
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Box(Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Category: $selectedCategory")
                    }
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEvents) { event ->
                    EventCard(
                        event = event,
                        onParticipate = { onParticipateClick(event) },
                        onDelete = { viewModel.delete(event) },
                        onView = { onViewClick(event) },
                        isCompleted = event.date < currentTime
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onParticipate: () -> Unit,
    onDelete: () -> Unit,
    onView: () -> Unit,
    isCompleted: Boolean
) {
    val cardColor = when (event.category) {
        "Tech" -> Color(0xFFE3F2FD)
        "Sports" -> Color(0xFFE8F5E9)
        "Cultural" -> Color(0xFFF3E5F5)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Event",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(
                text = "Date: ${formatDate(event.date)}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Location: ${event.location}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Category: ${event.category}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View")
                }
                Button(
                    onClick = onParticipate,
                    enabled = !isCompleted,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Participate")
                }
            }
        }
    }
}