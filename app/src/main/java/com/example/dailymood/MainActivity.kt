package com.example.dailymood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailymood.data.local.MoodDatabaseProvider
import com.example.dailymood.model.MoodType
import com.example.dailymood.ui.MoodViewModel
import com.example.dailymood.ui.MoodViewModelFactory
import com.example.dailymood.ui.screens.AddMoodScreen
import com.example.dailymood.ui.screens.MoodListScreen
import com.example.dailymood.ui.theme.DailyMoodTheme

class MainActivity : ComponentActivity() {

    // ViewModel med Room-DAO via factory
    private val moodViewModel: MoodViewModel by viewModels {
        val db = MoodDatabaseProvider.getDatabase(applicationContext)
        MoodViewModelFactory(db.moodDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DailyMoodTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    DailyMoodApp(moodViewModel = moodViewModel)
                }
            }
        }
    }
}

// Huvud-Composable som sköter navigationen
@Composable
fun DailyMoodApp(
    moodViewModel: MoodViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "mood_list"
    ) {
        composable("mood_list") {
            val moods by moodViewModel.moodList.collectAsState()
            val advice by moodViewModel.adviceText.collectAsState()
            val adviceLoading by moodViewModel.adviceLoading.collectAsState()
            val adviceError by moodViewModel.adviceError.collectAsState()

            MoodListScreen(
                moods = moods,
                adviceText = advice,
                isAdviceLoading = adviceLoading,
                adviceError = adviceError,
                onRefreshAdvice = { moodViewModel.loadAdvice() },
                onAddMoodClick = { navController.navigate("add_mood") },
                onDeleteMood = { moodEntry ->
                    moodViewModel.deleteMood(moodEntry)
                },
                onClearAll = {
                    moodViewModel.deleteAllMoods()
                }
            )
        }

        composable("add_mood") {
            AddMoodScreen(
                onSave = { moodType: MoodType, note: String ->
                    moodViewModel.addMood(moodType, note)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Enkel preview som inte använder riktiga ViewModel-data
@Preview(showBackground = true)
@Composable
fun DailyMoodPreview() {
    DailyMoodTheme {
        MoodListScreen(
            moods = emptyList(),
            adviceText = "Drink some water and take a short walk.",
            isAdviceLoading = false,
            adviceError = null,
            onRefreshAdvice = {},
            onAddMoodClick = {},
            onDeleteMood = {},
            onClearAll = {}
        )
    }
}
