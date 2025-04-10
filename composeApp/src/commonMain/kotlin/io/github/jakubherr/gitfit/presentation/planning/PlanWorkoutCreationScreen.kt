package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_exercise
import gitfit.composeapp.generated.resources.confirm
import gitfit.composeapp.generated.resources.delete_exercise
import gitfit.composeapp.generated.resources.edit_progression
import gitfit.composeapp.generated.resources.plan_name
import gitfit.composeapp.generated.resources.show_exercise_dropdown_menu
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.shared.PlanBlockItem
import io.github.jakubherr.gitfit.presentation.shared.StringInputField
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlanWorkoutCreationScreen(
    workoutPlan: WorkoutPlan,
    modifier: Modifier = Modifier,
    onAction: (PlanAction) -> Unit = {},
    onAddExerciseClick: (Int) -> Unit = {},
    onSave: () -> Unit = {},
    onEditProgression: (Block) -> Unit = {},
) {
    Column(
        modifier.padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            StringInputField(
                value = workoutPlan.name,
                onValueChange = { onAction(PlanAction.RenameWorkout(workoutPlan, it)) },
                maxLength = 20,
                label = { Text(stringResource(Res.string.plan_name)) },
                isError = workoutPlan.name.isBlank(),
            )
        }

        LazyColumn(
            Modifier.weight(1.0f),
        ) {
            items(workoutPlan.blocks) { block ->
                PlanBlockItem(
                    block,
                    onAddSetClicked = { onAction(PlanAction.AddSet(workoutPlan, block)) },
                    onValidSetEntered = { onAction(PlanAction.EditSet(workoutPlan, block, it)) },
                    onDeleteSeries = { onAction(PlanAction.RemoveSet(workoutPlan, block, it)) },
                    onDeleteExercise = { onAction(PlanAction.RemoveExercise(workoutPlan, block)) },
                    onEditProgression = { onEditProgression(block) },
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = { onAddExerciseClick(workoutPlan.idx) }) { Text(stringResource(Res.string.add_exercise)) }

            Button({
                onAction(PlanAction.ValidateWorkout(workoutPlan))
                onSave()
            }) { Text(stringResource(Res.string.confirm)) }
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
            Icon(Icons.Default.MoreVert, stringResource(Res.string.show_exercise_dropdown_menu))
        }

        DropdownMenu(
            expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.delete_exercise)) },
                onClick = {
                    onDeleteExercise()
                    expanded = false
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.edit_progression)) },
                onClick = {
                    onEditProgression()
                    expanded = false
                },
            )
        }
    }
}
