package com.example.dailymood.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Detta är hur posten ser ut i databasen (bara enkla typer).
// This is how the entry looks like in the database (only simple types).
@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dateString: String,   // t.ex. "2025-12-09"
    val moodName: String,     // t.ex. "HAPPY"
    val note: String
)
