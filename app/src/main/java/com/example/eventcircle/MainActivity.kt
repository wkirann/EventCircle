package com.example.eventcircle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcircle.data.model.Event
import com.example.eventcircle.screens.AddEventScreen
import com.example.eventcircle.screens.EventDetailScreen
import com.example.eventcircle.screens.EventListScreen
import com.example.eventcircle.screens.RegistrationScreen
import com.example.eventcircle.screens.RegisteredEventsScreen
import com.example.eventcircle.screens.SplashScreen
import com.example.eventcircle.ui.theme.EventCircleTheme
import com.example.eventcircle.viewmodel.EventViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.util.Calendar
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventCircleTheme {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                EventCircleApp()
            }
        }
    }
}

@Composable
fun EventCircleApp() {
    val navController = rememberNavController()
    val viewModel: EventViewModel = viewModel()

    // Preload sample events only if the database is empty
    LaunchedEffect(Unit) {
        val events = viewModel.allEvents.first() // Get current events
        if (events.isEmpty()) { // Insert only if no events exist
            val calendar = Calendar.getInstance()

            // Event 1: Tech Conference (Future)
            calendar.set(2025, Calendar.MAY, 1, 10, 0)
            viewModel.insert(
                Event(
                    title = "Tech Conference 2025",
                    date = calendar.timeInMillis,
                    location = "Bangalore, India",
                    description = "A conference for tech enthusiasts to explore the latest trends in AI and software development.",
                    category = "Tech"
                )
            )

            // Event 2: Sports Day (Future)
            calendar.set(2025, Calendar.APRIL, 20, 9, 0)
            viewModel.insert(
                Event(
                    title = "Annual Sports Day",
                    date = calendar.timeInMillis,
                    location = "Mumbai, India",
                    description = "Join us for a day of fun sports activities and competitions.",
                    category = "Sports"
                )
            )

            // Event 3: Cultural Fest (Past)
            calendar.set(2025, Calendar.MARCH, 10, 18, 0)
            viewModel.insert(
                Event(
                    title = "Cultural Fest 2025",
                    date = calendar.timeInMillis,
                    location = "Delhi, India",
                    description = "Celebrate the rich culture with music, dance, and food.",
                    category = "Cultural"
                )
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("splash") {
            SplashScreen(
                onContinueClick = { navController.navigate("event_list") },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable("event_list") {
            EventListScreen(
                viewModel = viewModel,
                onAddEventClick = { navController.navigate("add_event") },
                onParticipateClick = { event ->
                    navController.navigate("registration/${event.id}")
                },
                onViewClick = { event ->
                    navController.navigate("event_detail/${event.id}")
                },
                onRegisteredEventsClick = { navController.navigate("registered_events") },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable("add_event") {
            AddEventScreen(
                viewModel = viewModel,
                onEventAdded = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = "registration/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            val event = viewModel.allEvents.collectAsState(initial = emptyList()).value
                .find { it.id == eventId }
            if (event != null) {
                RegistrationScreen(
                    event = event,
                    viewModel = viewModel,
                    onRegister = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Event not found", modifier = Modifier.padding(16.dp))
            }
        }
        composable(
            route = "event_detail/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            val event = viewModel.allEvents.collectAsState(initial = emptyList()).value
                .find { it.id == eventId }
            if (event != null) {
                EventDetailScreen(
                    event = event,
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Event not found", modifier = Modifier.padding(16.dp))
            }
        }
        composable("registered_events") {
            RegisteredEventsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}