package com.example.dailymood.model

import java.time.LocalDate

// En enkel data-klass för en humörpost
// A simple data class for a mood entry
data class MoodEntry(
    val id: Long,              // unik id / unique id
    val date: LocalDate,       // datum / date
    val mood: MoodType,        // typ av humör / mood type
    val note: String = ""      // valfri kommentar / optional note
)

// Olika humörtyper (du kan lägga till fler emojis senare)
// Different mood types (you can add more emojis later)
enum class MoodType(val emoji: String, val label: String) {
    HAPPY("😊", "Glad"),
    NEUTRAL("😐", "Okej"),
    SAD("😢", "Ledsen"),
    ANGRY("😡", "Arg"),
    TIRED("😴", "Trött");
}
