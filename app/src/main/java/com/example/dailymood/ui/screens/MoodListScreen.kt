package com.example.dailymood.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.dailymood.R
import com.example.dailymood.model.MoodEntry
import com.example.dailymood.showTestNotification

// Startskärmen – visar daily advice + lista med humör och en knapp för att lägga till nytt.
// Home screen – shows daily advice + mood history and a button to add new mood.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodListScreen(
    moods: List<MoodEntry>,
    adviceText: String?,
    isAdviceLoading: Boolean,
    adviceError: String?,
    onRefreshAdvice: () -> Unit,
    onAddMoodClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_daily_mood)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMoodClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.title_log_mood)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ---------- Daily advice + notifications card ----------
            AdviceAndNotificationCard(
                adviceText = adviceText,
                isAdviceLoading = isAdviceLoading,
                adviceError = adviceError,
                onRefreshAdvice = onRefreshAdvice
            )

            // ---------- Mood list / historik ----------
            if (moods.isEmpty()) {
                // Visa tomt tillstånd / show empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(id = R.string.empty_list_text))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(moods) { entry ->
                        MoodListItem(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdviceAndNotificationCard(
    adviceText: String?,
    isAdviceLoading: Boolean,
    adviceError: String?,
    onRefreshAdvice: () -> Unit
) {
    val context = LocalContext.current

    // Launcher för runtime-permission
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                // Om användaren godkänner, skicka en testnotis
                showTestNotification(context)
            }
        }

    // Kolla om vi redan har permission
    val hasNotificationPermission =
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    val notificationButtonTextRes =
        if (hasNotificationPermission || Build.VERSION.SDK_INT < 33) {
            R.string.notifications_send_test
        } else {
            R.string.notifications_enable
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // ----- Daily advice -----
            Text(
                text = stringResource(id = R.string.daily_advice_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isAdviceLoading -> {
                    CircularProgressIndicator()
                }

                adviceError != null -> {
                    Text(
                        text = adviceError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                adviceText != null -> {
                    Text(
                        text = adviceText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.daily_advice_hint),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onRefreshAdvice) {
                Text(stringResource(id = R.string.daily_advice_button))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----- Notifications -----
            Text(
                text = stringResource(id = R.string.notifications_title),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (hasNotificationPermission || Build.VERSION.SDK_INT < 33) {
                        // Har redan permission -> skicka bara notis
                        showTestNotification(context)
                    } else {
                        // Be om permission (Android 13+)
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }
            ) {
                Text(stringResource(id = notificationButtonTextRes))
            }
        }
    }
}

@Composable
private fun MoodListItem(entry: MoodEntry) {
    // En rad i listan
    // One row in the list
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = "${entry.mood.emoji} ${entry.mood.label}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = entry.date.toString(),
            style = MaterialTheme.typography.bodySmall
        )
        if (entry.note.isNotBlank()) {
            Text(
                text = entry.note,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}
