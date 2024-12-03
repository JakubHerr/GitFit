package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.Exercise


// Use case: Add custom exercise
@Composable
fun CreateExerciseScreenRoot(
    modifier: Modifier = Modifier
) {
    CreateExerciseScreen(Modifier.fillMaxSize())
}

@Composable
fun CreateExerciseScreen(
    modifier: Modifier = Modifier,
    onSaveExercise: (Exercise) -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // name
        TextField(
            "",
            {},
            label = { Text("Name") }
        )
        // description

        // primary muscles
        
        // secondary muscles
    }
}
