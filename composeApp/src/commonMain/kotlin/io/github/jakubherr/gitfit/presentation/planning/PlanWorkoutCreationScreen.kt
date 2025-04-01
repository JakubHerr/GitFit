package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.shared.PlanBlockItem

@Composable
fun PlanWorkoutCreationScreen(
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
                onDeleteSeries =  { onAction(PlanAction.RemoveSet(workout, block, it)) },
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
                onClick = {
                    onDeleteExercise()
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Edit progression") },
                onClick = {
                    onEditProgression()
                    expanded = false
                }
            )
        }
    }
}
