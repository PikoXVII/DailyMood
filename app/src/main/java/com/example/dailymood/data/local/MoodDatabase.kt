package com.example.dailymood.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MoodEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
}
