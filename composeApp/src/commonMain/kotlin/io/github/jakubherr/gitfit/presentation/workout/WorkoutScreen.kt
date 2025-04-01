package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_exercise
import gitfit.composeapp.generated.resources.add_set
import gitfit.composeapp.generated.resources.delete_workout
import gitfit.composeapp.generated.resources.done
import gitfit.composeapp.generated.resources.kg
import gitfit.composeapp.generated.resources.reps
import gitfit.composeapp.generated.resources.save_workout
import gitfit.composeapp.generated.resources.set
import io.github.jakubherr.gitfit.domain.isNonNegative
import io.github.jakubherr.gitfit.domain.isNonNegativeDouble
import io.github.jakubherr.gitfit.domain.isNonNegativeInt
import io.github.jakubherr.gitfit.domain.isNonNegativeLong
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.domain.model.mockSeries
import io.github.jakubherr.gitfit.presentation.shared.CheckableSetInput
import io.github.jakubherr.gitfit.presentation.shared.DoubleInputField
import io.github.jakubherr.gitfit.presentation.shared.IntegerInputField
import io.github.jakubherr.gitfit.presentation.shared.SetInput
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// use case: track a workout while in the gym
@Composable
fun WorkoutScreenRoot(
    vm: WorkoutViewModel = koinViewModel(),
    onAction: (WorkoutAction) -> Unit = {},
    onSaveComplete: () -> Unit = {}, // this mainly prevents cancelling viewmodel before it handles progression
) {
    // TODO loading indicator when workout modification hangs
    val workout by vm.currentWorkout.collectAsStateWithLifecycle(null)
    val workoutSaved = vm.workoutSaved

    LaunchedEffect(workoutSaved) {
        if (workoutSaved) onSaveComplete()
    }

    if (workout == null) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
        }
    } else {
        WorkoutScreen(workout!!) { action ->
            onAction(action)
        }
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
                Button({ onAction(WorkoutAction.AskForExercise(workout.id)) }) {
                    Text(stringResource(Res.string.add_exercise))
                }

                LazyColumn(Modifier.fillMaxSize().weight(1f)) {
                    items(workout.blocks) { block ->
                        BlockItem(
                            block,
                            onAction = onAction,
                            onAddSetClicked = {
                                val set = Series(block.series.size, null, null, false)
                                onAction(WorkoutAction.AddSet(workout.id, block.idx, set))
                            },
                        )
                    }
                }

                Row(Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(onClick = { onAction(WorkoutAction.DeleteWorkout(workout.id)) }) { // TODO "are you sure?" dialog
                        Text(stringResource(Res.string.delete_workout))
                    }
                    Button(onClick = { onAction(WorkoutAction.CompleteCurrentWorkout) }) {
                        Text(stringResource(Res.string.save_workout))
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
    onAddSetClicked: () -> Unit = {},
) {
    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text(block.exercise.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Column {
                SetHeader()
                Spacer(Modifier.height(16.dp))

                block.series.forEachIndexed { seriesIdx, series ->
                    CheckableSetInput(
                        seriesIdx,
                        series,
                        onToggle = { weight, reps ->
                            onAction(
                                WorkoutAction.ModifySeries(
                                    block.idx,
                                    series.copy(
                                        weight = weight.toDouble(),
                                        repetitions = reps.toLong(),
                                        completed = !series.completed,
                                    ),
                                ),
                            )
                        },
                    )
                }
            }
            Spacer(modifier.height(8.dp))
            Button(onClick = onAddSetClicked) {
                Text(stringResource(Res.string.add_set))
            }
        }
    }
}

// maybe easier to just have this info inline than trying to line up two different rows
@Composable
fun SetHeader(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(stringResource(Res.string.set))
        Text(stringResource(Res.string.kg))
        Text(stringResource(Res.string.reps))
        Text(stringResource(Res.string.done))
    }
}
