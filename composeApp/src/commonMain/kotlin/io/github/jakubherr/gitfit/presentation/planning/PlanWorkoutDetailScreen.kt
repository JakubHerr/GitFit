package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_set
import io.github.jakubherr.gitfit.domain.isPositiveDouble
import io.github.jakubherr.gitfit.domain.isPositiveLong
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.workout.NumberInputField
import io.github.jakubherr.gitfit.presentation.workout.SetHeader
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlanWorkoutDetailScreen(
    workout: WorkoutPlan,
    modifier: Modifier = Modifier,
    onAction: (PlanAction) -> Unit = {},
    onAddExerciseClick: (Int) -> Unit = {},
    onSave: () -> Unit = {},
) {
    LazyColumn {
        items(workout.blocks) { block ->
            PlanBlockItem(
                block,
                onAddSetClicked = { onAction(PlanAction.AddSet(workout, block)) },
                onValidSetEntered = { onAction(PlanAction.EditSet(workout, block, it)) },
                onDeleteSet =  { onAction(PlanAction.RemoveSet(workout, block, it)) },
                onDeleteExercise = { onAction(PlanAction.RemoveExercise(workout, block)) },
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
    onDeleteExercise: (Block) -> Unit = {},
) {
    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(8.dp)) {
            Row {
                Text(block.exercise.name, style = MaterialTheme.typography.titleLarge)
                IconButton({ onDeleteExercise(block) }) {
                    Icon(Icons.Default.Delete, "")
                }
            }
            Spacer(Modifier.height(16.dp))
            Column {
                SetHeader()
                Spacer(Modifier.height(16.dp))
                block.series.forEachIndexed { idx, set ->
                    PlanSetInput(
                        idx + 1, set,
                        onValidSetEntered = { onValidSetEntered(it) },
                        onDeleteSet = { onDeleteSet(set) }
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
fun PlanSetInput(
    index: Int,
    set: Series,
    modifier: Modifier = Modifier,
    onValidSetEntered: (Series) -> Unit = {},
    onDeleteSet: () -> Unit = {}
) {
    var weight by remember { mutableStateOf(set.weight?.toString() ?: "") }
    var reps by remember { mutableStateOf(set.repetitions?.toString() ?: "") }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(index.toString())

        NumberInputField(
            weight,
            isError = !weight.isPositiveDouble(),
            onValueChange = { newWeight ->
                // limits the number of decimal points to 2
                val decimals = newWeight.substringAfter(".", "")

                if (decimals.length <= 2) {
                    weight = newWeight

                    if (weight.isPositiveDouble() && reps.isPositiveLong()) {
                        onValidSetEntered(set.copy(weight = weight.toDouble(), repetitions = reps.toLong()))
                    }
                }
            }
        )
        NumberInputField(
            reps,
            isError = !reps.isPositiveLong(),
            onValueChange = {
                reps = it
                if (weight.isPositiveDouble() && reps.isPositiveLong()) {
                    onValidSetEntered(set.copy(weight = weight.toDouble(), repetitions = reps.toLong()))
                }
            }
        )

        IconButton(onDeleteSet) {
            Icon(Icons.Default.Delete, "")
        }
    }
}