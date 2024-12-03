package io.github.jakubherr.gitfit.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.mockExercise
import io.github.jakubherr.gitfit.domain.mockSeries
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkoutScreenRoot() {
    val vm = koinViewModel<WorkoutViewModel>()
    val state = vm.state.collectAsStateWithLifecycle()
    WorkoutScreen(state.value) { action ->
        vm.onAction(action)
    }
}

@Composable
fun WorkoutScreen(
    workout: Workout,
    onAction: (WorkoutAction) -> Unit = {},
) {
    Scaffold { padding ->
        Surface {
            Column(Modifier.padding(padding)) {
                Button({ onAction(WorkoutAction.AddBlock(mockExercise)) }) {
                    Text("Add exercise")
                }

                LazyColumn(Modifier.fillMaxSize()) {
                    items(workout.blocks) { block ->
                        BlockItem(block, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun BlockItem(
    block: Block,
    modifier: Modifier = Modifier,
    onAction: (WorkoutAction) -> Unit = {},
) {
    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text(block.exercise.name, style = MaterialTheme.typography.titleLarge)
            Column {
                SetHeader()
                block.series.forEachIndexed { idx, series ->
                    SetItem(idx + 1, series)
                }
            }
            Spacer(modifier.height(8.dp))
            Button(onClick = { onAction(WorkoutAction.AddSet(block.id, mockSeries)) }) {
                Text("Add set")
            }
        }
    }
}

// maybe easier to just have this info inline than trying to line up two different rows
@Composable
fun SetHeader(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Set")
        Text("KG")
        Text("Reps")
        Text("Done")
    }
}

@Composable
fun SetItem(
    index: Int,
    set: Series = mockSeries,
    modifier: Modifier = Modifier,
    onAction: (WorkoutAction) -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(index.toString())
        Text((set.weight ?: 0).toString())

        OutlinedTextField(value = "", {}, modifier = Modifier.width(64.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = "", {}, modifier = Modifier.width(64.dp))

        Text((set.repetitions ?: 0).toString())
        Checkbox(set.completed, onCheckedChange = { onAction(WorkoutAction.ToggleSetCompletion(set.id))})
    }
}

