package com.example.eventcircle.data

import android.content.Context
import androidx.room.Room
import com.example.eventcircle.data.AppDatabase

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "eventcircle_database"
            )
                .build()
                .also { instance = it }
        }
    }
}