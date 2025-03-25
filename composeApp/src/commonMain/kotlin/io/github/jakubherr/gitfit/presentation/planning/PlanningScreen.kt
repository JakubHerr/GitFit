package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.BlockItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.viewmodel.koinViewModel

// use case: plan workouts to be performed in the future (weeks, months)
@Composable
fun PlanningScreenRoot(
    vm: PlanningViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    var creatingPlan by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize()) {
        if (!creatingPlan) {
            Text("Your plans")
            LazyColumn {
                items(3) { idx -> Text("Plan #$idx") }
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

    Text("Selected workout: $workout")

    Button({ selectExercise = true}) {
        Text("Add exercise")
    }

    LazyColumn {
        items(workout.blocks) { block ->
            BlockItem(
                block,
                onAction = { onAction(PlanAction.RemoveExercise(workout, block)) }
            ) {
                onAction(PlanAction.AddSet(workout, block))
            }
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
