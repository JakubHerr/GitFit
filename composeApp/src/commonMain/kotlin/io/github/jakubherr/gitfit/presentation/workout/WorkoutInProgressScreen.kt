package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import gitfit.composeapp.generated.resources.delete_workout
import gitfit.composeapp.generated.resources.done
import gitfit.composeapp.generated.resources.kg
import gitfit.composeapp.generated.resources.reps
import gitfit.composeapp.generated.resources.save_workout
import gitfit.composeapp.generated.resources.set
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import io.github.jakubherr.gitfit.presentation.shared.WorkoutBlockItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun WorkoutInProgressScreenRoot(
    vm: WorkoutViewModel,
    onAction: (WorkoutAction) -> Unit = {},
    onSaveComplete: () -> Unit = {}, // this mainly prevents cancelling viewmodel before it handles progression
) {
    // TODO loading indicator when workout modification hangs
    val workout by vm.currentWorkout.collectAsStateWithLifecycle(null)
    val workoutSaved = vm.workoutSaved
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(workoutSaved) {
        if (workoutSaved) onSaveComplete()
    }

    if (showDialog) {
        ConfirmationDialog(
            title = "Delete workout",
            text = "The current workout will be deleted",
            confirmText = "Delete workout",
            dismissText = "Continue workout",
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                workout?.let {
                    onAction(WorkoutAction.DeleteWorkout(it.id))
                }
            }
        )
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
        WorkoutInProgressScreen(workout!!) { action ->
            if (action is WorkoutAction.DeleteWorkout) showDialog = true
            else onAction(action)
        }
    }
}

@Composable
fun WorkoutInProgressScreen(
    workout: Workout,
    onAction: (WorkoutAction) -> Unit = {},
) {
    Scaffold { padding ->
        Surface {
            Column(Modifier.padding(padding)) {
                LazyColumn(
                    Modifier.fillMaxSize().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(workout.blocks) { block ->
                        WorkoutBlockItem(
                            workout,
                            block,
                            onAction = onAction,
                            onAddSetClicked = {
                                onAction(WorkoutAction.AddSet(workout, block.idx))
                            },
                            dropdownMenu = {
                                WorkoutBlockItemDropdownMenu(
                                    onDeleteExercise = { onAction(WorkoutAction.RemoveBlock(workout, block) )},
                                    onDeleteSet = {
                                        val series = block.series.lastOrNull()
                                        series?.let {
                                            onAction(WorkoutAction.DeleteLastSeries(workout, block.idx, it))
                                        }
                                    }
                                )
                            }
                        )
                    }
                    item {
                        Button({ onAction(WorkoutAction.AskForExercise(workout.id)) }) {
                            Text(stringResource(Res.string.add_exercise))
                        }
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
fun WorkoutBlockItemDropdownMenu(
    modifier: Modifier = Modifier,
    onDeleteExercise: () -> Unit = {},
    onDeleteSet: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton({ expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, "")
        }

        DropdownMenu(
            expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Delete exercise") },
                onClick = {
                    expanded = false
                    onDeleteExercise()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete last set") },
                onClick = {
                    expanded = false
                    onDeleteSet()
                }
            )
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
