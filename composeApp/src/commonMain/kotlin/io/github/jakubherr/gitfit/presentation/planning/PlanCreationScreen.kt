package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan

@Composable
fun PlanCreationScreen(
    plan: Plan,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
    onWorkoutSelected: (Int) -> Unit = {},
    onAction: (PlanAction) -> Unit = {},
) {
    var planName by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize()) {
        Text("Creating a new plan")
        Text("Plan name")
        TextField(value = plan.name, onValueChange = { planName = it })

        LazyColumn {
            items(plan.workouts) { workout ->
                PlanListItem(workout) {
                    onWorkoutSelected(workout.idx)
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

        Button({
            onAction(PlanAction.SavePlan)
            onFinished()
        }) {
            Text("Save plan")
        }
        Button({ /* TODO */ }) {
            Text("Cancel")
        }
    }
}