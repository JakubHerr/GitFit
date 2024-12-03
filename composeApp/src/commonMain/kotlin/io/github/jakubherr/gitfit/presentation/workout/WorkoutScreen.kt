package io.github.jakubherr.gitfit.presentation.workout

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.mockExercise
import io.github.jakubherr.gitfit.domain.mockSeries
import org.koin.compose.viewmodel.koinViewModel

// use case: track a workout while in the gym
@Composable
fun WorkoutScreenRoot(
    vm: WorkoutViewModel = koinViewModel(),
    onAddExerciseClick: () -> Unit = {},
) {
    val state = vm.state.collectAsStateWithLifecycle()
    WorkoutScreen(state.value) { action ->
        if (action is WorkoutAction.AddBlock) onAddExerciseClick()
        vm.onAction(action)
    }
}

// TODO add options to definitively save workout and discard workout
//  maybe save workout in progress somehow to survive process death
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

                LazyColumn(Modifier.fillMaxSize().weight(1f)) {
                    items(workout.blocks) { block ->
                        BlockItem(block, onAction = onAction)
                    }
                }

                Row(Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(onClick = {}) {
                        Text("delete")
                    }
                    Button(onClick = {}) {
                        Text("save")
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
            Spacer(Modifier.height(16.dp))
            Column {
                SetHeader()
                Spacer(Modifier.height(16.dp))
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
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(index.toString())

        NumberInputField(weight, onValueChange = { weight = it })
        NumberInputField(reps, onValueChange = { reps = it })

        // TODO sanity check values before saving
        Checkbox(
            set.completed,
            onCheckedChange = { onAction(WorkoutAction.ToggleSetCompletion(set.id)) }
        )
    }
}

@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: Int = 0,
) {
    OutlinedTextField(
        value,
        onValueChange,
        placeholder = { Text(placeholder.toString(), modifier.alpha(0.6f)) },
        modifier = Modifier.width(64.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}

