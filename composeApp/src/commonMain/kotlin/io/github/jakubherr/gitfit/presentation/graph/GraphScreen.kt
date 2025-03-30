package io.github.jakubherr.gitfit.presentation.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import kotlin.enums.EnumEntries

// use case: browse previous records, measurements over a month/year
@Composable
fun GraphScreenRoot(
    vm: GraphViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onShowExerciseData: () -> Unit = { },
) {
    val measurements by vm.allUserMeasurements.collectAsStateWithLifecycle(emptyList())

    Column(
        modifier.fillMaxSize()
    ) {
        Button(onShowExerciseData) {
            Text("Browse exercise history")
        }

        Text("User measurements")
        LazyColumn {
            items(measurements) { measurement ->
                Text(measurement.date.toString())
            }
        }


        // TODO: graphs that visualize user body measurements
    }
}

enum class ExerciseMetric{
    HEAVIEST_WEIGHT,
    BEST_SET_VOLUME,
    TOTAL_WORKOUT_VOLUME,
    TOTAL_REPETITIONS
}

@Composable
fun <T: Enum<T>> SingleChoiceChipSelection(
    choices: EnumEntries<T>,
    selected: T,
    modifier: Modifier = Modifier,
    onChoiceSelected: (T) -> Unit = {},
) {
    LazyRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(choices) { choice ->
            FilterChip(
                selected = choice == selected,
                onClick = { onChoiceSelected(choice) },
                label = { Text(choice.name) }
            )
        }
    }
}
