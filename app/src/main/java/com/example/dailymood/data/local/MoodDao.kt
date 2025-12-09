package com.example.dailymood.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Query("SELECT * FROM moods ORDER BY dateString DESC, id DESC")
    fun getAllMoods(): Flow<List<MoodEntity>>

    @Insert
    suspend fun insertMood(mood: MoodEntity)

    // 🔥 NYTT: Delete EN specifik mood-post (för Grade 4)
    @Delete
    suspend fun deleteMood(mood: MoodEntity)

    @Query("DELETE FROM moods")
    suspend fun deleteAll()
}
