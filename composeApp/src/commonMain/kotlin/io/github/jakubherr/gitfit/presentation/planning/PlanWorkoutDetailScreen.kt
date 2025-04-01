package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_set
import io.github.jakubherr.gitfit.domain.isNonNegativeDouble
import io.github.jakubherr.gitfit.domain.isNonNegativeLong
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.shared.SetInput
import io.github.jakubherr.gitfit.presentation.workout.SetHeader
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlanWorkoutDetailScreen(
    workout: WorkoutPlan,
    modifier: Modifier = Modifier,
    onAction: (PlanAction) -> Unit = {},
    onAddExerciseClick: (Int) -> Unit = {},
    onSave: () -> Unit = {},
    onEditProgression: (Block) -> Unit = {},
) {
    LazyColumn {
        items(workout.blocks) { block ->
            PlanBlockItem(
                block,
                onAddSetClicked = { onAction(PlanAction.AddSet(workout, block)) },
                onValidSetEntered = { onAction(PlanAction.EditSet(workout, block, it)) },
                onDeleteSet =  { onAction(PlanAction.RemoveSet(workout, block, it)) },
                onDeleteExercise = { onAction(PlanAction.RemoveExercise(workout, block)) },
                onEditProgression = { onEditProgression(block) }
            )
        }
        item {
            Button(onClick = { onAddExerciseClick(workout.idx) }) { Text("Add exercise") }
        }
        item {
            Button({
                onAction(PlanAction.SaveWorkout(workout))
                onSave()
            }) { Text("Save workout") }
        }
    }
}

@Composable
fun PlanBlockItem(
    block: Block,
    modifier: Modifier = Modifier,
    onAddSetClicked: () -> Unit = {},
    onValidSetEntered: (Series) -> Unit = {},
    onDeleteSet: (Series) -> Unit = {},
    onDeleteExercise: () -> Unit = {},
    onEditProgression: () -> Unit = {},
) {
    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(Modifier.weight(1.0f)) {
                    Text(block.exercise.name, style = MaterialTheme.typography.titleLarge)
                }

                PlanBlockItemDropdownMenu(
                    onDeleteExercise = { onDeleteExercise() },
                    onEditProgression = { onEditProgression() }
                )
            }
            HorizontalDivider(Modifier.padding(8.dp))
            Column {
                SetHeader()
                Spacer(Modifier.height(16.dp))
                block.series.forEachIndexed { idx, set ->
                    SetInput(
                        idx,
                        set,
                        modifier,
                        validator = { weight, reps -> weight.isNonNegativeDouble() && reps.isNonNegativeLong()},
                        onValidSetEntered = { onValidSetEntered(it) },
                        actionSlot = {
                            IconButton({ onDeleteSet(set) }) {
                                Icon(Icons.Default.Delete, "")
                            }
                        }
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

@Composable
fun PlanBlockItemDropdownMenu(
    modifier: Modifier = Modifier,
    onDeleteExercise: () -> Unit = {},
    onEditProgression: () -> Unit = {},
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
                onClick = { onDeleteExercise() }
            )
            DropdownMenuItem(
                text = { Text("Edit progression") },
                onClick = { onEditProgression() }
            )
        }
    }
}
