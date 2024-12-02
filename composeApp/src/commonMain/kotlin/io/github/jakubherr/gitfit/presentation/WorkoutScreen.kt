package io.github.jakubherr.gitfit.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.mockSeries
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkoutScreenRoot(modifier: Modifier = Modifier) {
    val vm = koinViewModel<WorkoutViewModel>()
    val state = vm.state.collectAsStateWithLifecycle()
    WorkoutScreen(state.value)
}

@Composable
fun WorkoutScreen(
    workout: Workout,
    // onAction WorkoutAction
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        Column(Modifier.fillMaxSize()) {
            Button({ /* TODO */ }) {
                Text("Add exercise")
            }

            LazyColumn {
                items(workout.blocks) { block ->
                    BlockItem(block)
                }
            }
        }
    }
}

@Composable
fun BlockItem(
    block: Block,
    modifier: Modifier = Modifier
) {
    Card(Modifier.fillMaxWidth()) {
        Column {
            Text(block.exercise.name)
            Column {
                SetHeader()
                block.series.forEachIndexed { idx, series ->
                    SetItem(series)
                }
            }
            Spacer(modifier.height(8.dp))
            Button(onClick = { /* TODO */ }) {
                Text("Add set")
            }
        }
    }
}

@Composable
fun SetHeader(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        Text("Weight")
        Text("Reps")
    }
}

@Composable
fun SetItem(
    set: Series = mockSeries,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text((set.weight ?: 0).toString())
        Text((set.repetitions ?: 0).toString())
        Checkbox(set.completed, onCheckedChange = {
            // TODO
        })
    }
}

