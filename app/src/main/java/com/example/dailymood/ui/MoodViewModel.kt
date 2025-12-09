package com.example.dailymood.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailymood.data.local.MoodDao
import com.example.dailymood.data.local.MoodEntity
import com.example.dailymood.model.MoodEntry
import com.example.dailymood.model.MoodType
import com.example.dailymood.network.AdviceApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

// Hur länge (i millisekunder) vi ska fortsätta samla data efter att
// sista lyssnaren har slutat lyssna på flödet.
// How long (in ms) we keep the flow active after the last subscriber.
private const val STOP_TIMEOUT_MS: Long = 5_000L

// ViewModel tar emot ett MoodDao (Room) i konstruktorn.
// ViewModel receives a MoodDao (Room) in the constructor.
class MoodViewModel(
    private val moodDao: MoodDao
) : ViewModel() {

    // -------------------- MOOD LIST (Room) --------------------

    // Flöde från databasen -> vi mappar till vår "domänmodell" MoodEntry
    // Flow from database -> we map to our domain model MoodEntry
    val moodList: StateFlow<List<MoodEntry>> =
        moodDao.getAllMoods()
            .map { entities ->
                entities.map { it.toMoodEntry() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = emptyList()
            )

    // Lägg till ett nytt humör (sparas i databasen via Room)
    // Add new mood (saved in database via Room)
    fun addMood(mood: MoodType, note: String) {
        val today = LocalDate.now().toString() // "YYYY-MM-DD"

        val entity = MoodEntity(
            dateString = today,
            moodName = mood.name, // e.g. "HAPPY"
            note = note
        )

        viewModelScope.launch {
            moodDao.insertMood(entity)
        }
    }

    // Ta bort ett humör från databasen
    // Delete a single mood from the database
    fun deleteMood(entry: MoodEntry) {
        viewModelScope.launch {
            moodDao.deleteMood(entry.toMoodEntity())
        }
    }

    // Ta bort alla humörposter från databasen
    // Delete all moods from the database
    fun deleteAllMoods() {
        viewModelScope.launch {
            moodDao.deleteAll()
        }
    }

    // Helper: konvertera från Entity -> MoodEntry
    // Helper: convert from Entity -> MoodEntry
    private fun MoodEntity.toMoodEntry(): MoodEntry {
        val date = LocalDate.parse(dateString)

        val moodType = try {
            MoodType.valueOf(moodName)
        } catch (e: IllegalArgumentException) {
            MoodType.NEUTRAL
        }

        return MoodEntry(
            id = id,
            date = date,
            mood = moodType,
            note = note
        )
    }

    // Helper: konvertera från MoodEntry -> MoodEntity (används vid delete)
    // Helper: convert from MoodEntry -> MoodEntity (used for delete)
    private fun MoodEntry.toMoodEntity(): MoodEntity {
        return MoodEntity(
            id = this.id,
            dateString = this.date.toString(),
            moodName = this.mood.name,
            note = this.note
        )
    }

    // -------------------- DAILY ADVICE (API) --------------------

    // Texten för dagens råd / The text of today's advice
    private val _adviceText = MutableStateFlow<String?>(null)
    val adviceText: StateFlow<String?> = _adviceText

    // Laddningsflagga / Loading flag
    private val _adviceLoading = MutableStateFlow(false)
    val adviceLoading: StateFlow<Boolean> = _adviceLoading

    // Enkel feltext / Simple error text
    private val _adviceError = MutableStateFlow<String?>(null)
    val adviceError: StateFlow<String?> = _adviceError

    // Hämta ett råd från API:t / Fetch advice from the API
    fun loadAdvice() {
        viewModelScope.launch {
            _adviceLoading.value = true
            _adviceError.value = null
            try {
                val response = AdviceApiService.api.getAdvice()
                _adviceText.value = response.slip.advice
            } catch (e: Exception) {
                e.printStackTrace()
                _adviceError.value = "Could not load advice"
            } finally {
                _adviceLoading.value = false
            }
        }
    }
}
