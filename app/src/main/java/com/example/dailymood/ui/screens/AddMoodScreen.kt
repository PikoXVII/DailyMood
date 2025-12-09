package com.example.dailymood.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.dailymood.model.MoodType

// Skärm där man väljer humör + skriver en kort kommentar.
// Screen where user selects mood + optional note.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoodScreen(
    onSave: (MoodType, String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedMood by remember { mutableStateOf<MoodType?>(MoodType.HAPPY) }
    var note by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log today's mood") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "How do you feel today?",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                MoodChooser(
                    selected = selectedMood,
                    onMoodSelected = { selectedMood = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Short note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        // En enkel validering – kräver bara att ett mood är valt.
                        // Simple validation – just require a mood to be selected.
                        selectedMood?.let { mood ->
                            onSave(mood, note.text)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun MoodChooser(
    selected: MoodType?,
    onMoodSelected: (MoodType) -> Unit
) {
    Column {
        Text(
            text = "Select mood:",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Vi visar alla moods i en kolumn med knappar.
        // Show all moods as clickable rows
        MoodType.values().forEach { mood ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onMoodSelected(mood) },
                colors = if (mood == selected) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                } else {
                    CardDefaults.cardColors()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mood.emoji,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = mood.label,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
