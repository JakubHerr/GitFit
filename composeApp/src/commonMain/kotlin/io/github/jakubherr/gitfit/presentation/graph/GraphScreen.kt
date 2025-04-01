package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

// use case: browse previous records, measurements over a month/year
@Composable
fun GraphScreenRoot(
    vm: GraphViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onShowExerciseData: () -> Unit = { },
) {
    Column(
        modifier.fillMaxSize()
    ) {
        Button(onShowExerciseData) {
            Text("Browse exercise history")
        }
    }
}

enum class ExerciseMetric{
    HEAVIEST_WEIGHT,
    BEST_SET_VOLUME,
    TOTAL_WORKOUT_VOLUME,
    TOTAL_REPETITIONS
}

