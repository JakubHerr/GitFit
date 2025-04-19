package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.browse_exercise_history
import gitfit.composeapp.generated.resources.browse_measurement_history
import gitfit.composeapp.generated.resources.browse_workout_history
import org.jetbrains.compose.resources.stringResource

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBrowseExerciseData: () -> Unit = {},
    onBrowseMeasurementData: () -> Unit = {},
    onBrowseWorkoutData: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier.width(IntrinsicSize.Max).wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onBrowseExerciseData,
                Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.browse_exercise_history))
            }

            Button(
                onBrowseWorkoutData,
                Modifier.fillMaxWidth().testTag("BrowseWorkoutHistoryButton")
            ) {
                Text(stringResource(Res.string.browse_workout_history))
            }

            Button(
                onBrowseMeasurementData,
                Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.browse_measurement_history))
            }
        }
    }
}

enum class ExerciseMetric {
    HEAVIEST_WEIGHT,
    BEST_SET_VOLUME,
    TOTAL_WORKOUT_VOLUME,
    TOTAL_REPETITIONS,
}
