package com.example.dailymood.data.local

import android.content.Context
import androidx.room.Room

// Singleton som skapar databasen första gången den används.
// Singleton that creates the database the first time it is used.
object MoodDatabaseProvider {

    @Volatile
    private var instance: MoodDatabase? = null

    fun getDatabase(context: Context): MoodDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                MoodDatabase::class.java,
                "mood_database"
            ).build().also { db ->
                instance = db
            }
        }
    }
}
