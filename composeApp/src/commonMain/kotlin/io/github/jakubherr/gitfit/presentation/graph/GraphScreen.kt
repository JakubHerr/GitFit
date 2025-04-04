package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// use case: browse previous records, measurements, exercises etc.
@Composable
fun HistoryScreenRoot(
    modifier: Modifier = Modifier,
    onBrowseExerciseData: () -> Unit = {},
    onBrowseMeasurementData: () -> Unit = {},
    onBrowseWorkoutData: () -> Unit
) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onBrowseExerciseData) {
            Text("Browse exercise history")
        }

        Button(onBrowseWorkoutData) {
            Text("Browse workout history")
        }

        Button(onBrowseMeasurementData) {
            Text("Browse measurement history")
        }
    }
}

enum class ExerciseMetric{
    HEAVIEST_WEIGHT,
    BEST_SET_VOLUME,
    TOTAL_WORKOUT_VOLUME,
    TOTAL_REPETITIONS
}

