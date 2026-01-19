package com.example.eventcircle.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eventcircle.data.model.Event

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}