package com.example.eventcircle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventcircle.data.DatabaseProvider
import com.example.eventcircle.data.EventDao
import com.example.eventcircle.data.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val eventDao: EventDao = DatabaseProvider.getDatabase(application).eventDao()
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()
    private val _registeredEventIds = MutableStateFlow<Set<Int>>(emptySet())
    val registeredEventIds: StateFlow<Set<Int>> = _registeredEventIds.asStateFlow()

    fun insert(event: Event) {
        viewModelScope.launch {
            eventDao.insert(event)
        }
    }

    fun delete(event: Event) {
        viewModelScope.launch {
            eventDao.delete(event)
            _registeredEventIds.value = _registeredEventIds.value - event.id
        }
    }

    fun registerForEvent(eventId: Int) {
        viewModelScope.launch {
            _registeredEventIds.value = _registeredEventIds.value + eventId
        }
    }

    fun getEventsByCategory(category: String): Flow<List<Event>> {
        return eventDao.getEventsByCategory(category)
    }
}