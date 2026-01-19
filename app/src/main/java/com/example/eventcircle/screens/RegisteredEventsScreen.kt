package com.example.eventcircle.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcircle.data.model.Event
import com.example.eventcircle.utils.formatDate
import com.example.eventcircle.viewmodel.EventViewModel

@Composable
fun RegisteredEventsScreen(
    viewModel: EventViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsState(initial = emptyList())
    val registeredEventIds by viewModel.registeredEventIds.collectAsState()
    val registeredEvents = events.filter { it.id in registeredEventIds }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Registered Events",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(registeredEvents) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Date: ${formatDate(event.date)}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                        )
                        Text(
                            text = "Location: ${event.location}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                        )
                        Text(
                            text = "Category: ${event.category}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                        )
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Back")
        }
    }
}