package io.github.jakubherr.gitfit.presentation.planning

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_set
import io.github.jakubherr.gitfit.domain.isPositiveLong
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.domain.model.mockSeries
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.CheckableSetInput
import io.github.jakubherr.gitfit.presentation.workout.NumberInputField
import io.github.jakubherr.gitfit.presentation.workout.SetHeader
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// use case: plan workouts to be performed in the future (weeks, months)
@Composable
fun PlanningScreenRoot(
    vm: PlanningViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    var creatingPlan by remember { mutableStateOf(false) }
    val userWorkouts by vm.userWorkouts.collectAsStateWithLifecycle(emptyList())

    Column(modifier.fillMaxSize()) {
        if (!creatingPlan) {
            Text("Your plans")
            LazyColumn {
                items(userWorkouts) { wo ->
                    // TODO UI card for workout
                    Text(wo.blocks.joinToString())
                }
            }

            Text("Predefined plans")
            LazyColumn {
                items(3) { idx -> Text("Predefined plan #$idx") }
            }

            Button({ creatingPlan = true }) {
                Text("Create a new plan")
            }
        } else {
            PlanCreation(
                vm.plan,
                onFinished = { creatingPlan = false }
            ) {
                vm.onAction(it)
            }
        }
    }

}

@Composable
fun PlanCreation(
    plan: Plan,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
    onAction: (PlanAction) -> Unit = {},
) {
    var planName by remember { mutableStateOf("") }
    var selectedWorkoutIdx by remember { mutableStateOf<Int?>(null) }

    if (selectedWorkoutIdx == null) {
        Text("Creating a new plan")
        Text("Plan name")
        TextField(value = plan.name, onValueChange = { planName = it })

        LazyColumn {
            items(plan.workouts) { workout ->
                PlanListItem(workout) {
                    selectedWorkoutIdx = workout.idx
                }
            }
        }

        Button({
            val workout = WorkoutPlan(
                "New workout",
                plan.workouts.size,
                emptyList(),
            )

            onAction(PlanAction.AddWorkout(workout))
        }) {
            Text("Add workout day")
        }
        Button({ /* TODO */ }) {
            Text("Save plan")
        }
        Button({ /* TODO */ }) {
            Text("Cancel")
        }
    } else {
        PlanWorkoutDetail(plan.workouts[selectedWorkoutIdx!!]) {
            onAction(it)
        }
    }
}

@Composable
fun PlanListItem(
    workout: WorkoutPlan,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(onClick) {
        Column {
            Text(workout.name)
            Text(workout.blocks.joinToString())
        }
    }
}

@Composable
fun PlanWorkoutDetail(
    workout: WorkoutPlan,
    modifier: Modifier = Modifier,
    onAction: (PlanAction) -> Unit = {},
) {
    var selectExercise by remember { mutableStateOf(false) }

    LazyColumn {
        item {
            Button({ selectExercise = true }) { Text("Add exercise") }
        }
        items(workout.blocks) { block ->
            PlanBlockItem(
                block,
            ) {
                onAction(PlanAction.AddSet(workout, block))
            }
        }
        item {
            Button({ onAction(PlanAction.SaveWorkout(workout)) }) { Text("Save workout") }
        }
    }

    if (selectExercise) {
        ExerciseListScreenRoot(
            onExerciseClick = { exercise ->
                println("DBG: $exercise selected")

                onAction(PlanAction.AddExercise(workout, exercise))
                selectExercise = false
            }
        )
    }
}

@Composable
fun PlanBlockItem(
    block: Block,
    modifier: Modifier = Modifier,
    onAddSetClicked: () -> Unit = {},
) {
    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(8.dp)) {
            Row {
                Text(block.exercise.name, style = MaterialTheme.typography.titleLarge)
                IconButton({}) {
                    Icon(Icons.Default.MoreVert, "")
                }
            }
            Spacer(Modifier.height(16.dp))
            Column {
                SetHeader()
                Spacer(Modifier.height(16.dp))
                block.series.forEachIndexed { idx, set ->
                    PlanSetInput(idx+1, set)
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
    set: Series = mockSeries,
    modifier: Modifier = Modifier,
) {
    var weight by remember { mutableStateOf(set.weight?.toString() ?: "") }
    var reps by remember { mutableStateOf(set.repetitions?.toString() ?: "") }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(index.toString())

        NumberInputField(weight, onValueChange = { weight = it })
        NumberInputField(reps, onValueChange = { reps = it })
    }
}

