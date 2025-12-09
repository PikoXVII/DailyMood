package com.example.dailymood.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailymood.data.local.MoodDao

// Factory för att kunna skicka in MoodDao till ViewModel.
// Factory so we can pass MoodDao into the ViewModel.
class MoodViewModelFactory(
    private val moodDao: MoodDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
            return MoodViewModel(moodDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
