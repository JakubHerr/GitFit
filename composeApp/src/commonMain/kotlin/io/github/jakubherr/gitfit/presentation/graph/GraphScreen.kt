package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.browse_exercise_history
import gitfit.composeapp.generated.resources.browse_measurement_history
import gitfit.composeapp.generated.resources.browse_workout_history
import org.jetbrains.compose.resources.stringResource

@Composable
fun HistoryScreenRoot(
    modifier: Modifier = Modifier,
    onBrowseExerciseData: () -> Unit = {},
    onBrowseMeasurementData: () -> Unit = {},
    onBrowseWorkoutData: () -> Unit,
) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onBrowseExerciseData) {
            Text(stringResource(Res.string.browse_exercise_history))
        }

        Button(onBrowseWorkoutData) {
            Text(stringResource(Res.string.browse_workout_history))
        }

        Button(onBrowseMeasurementData) {
            Text(stringResource(Res.string.browse_measurement_history))
        }
    }
}

enum class ExerciseMetric {
    HEAVIEST_WEIGHT,
    BEST_SET_VOLUME,
    TOTAL_WORKOUT_VOLUME,
    TOTAL_REPETITIONS,
}
