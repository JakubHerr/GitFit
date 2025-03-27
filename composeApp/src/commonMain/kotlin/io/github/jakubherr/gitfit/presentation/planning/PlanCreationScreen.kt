package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan

@Composable
fun PlanCreationScreen(
    plan: Plan,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    onDiscard: () -> Unit = {},
    onWorkoutSelected: (Int) -> Unit = {},
    onAction: (PlanAction) -> Unit = {},
) {
    Column(modifier.fillMaxSize()) {
        Text("Creating a new plan")
        Text("Plan name")
        TextField(
            value = plan.name,
            onValueChange = { onAction(PlanAction.RenamePlan(it)) }
        )

        Spacer(Modifier.width(16.dp))

        Text("Workout days")
        LazyColumn {
            items(plan.workouts) { workout ->
                WorkoutListItem(
                    workout,
                    onClick = { onWorkoutSelected(workout.idx) },
                    onDeleteClicked = { onAction(PlanAction.DeleteWorkout(workout))}
                )
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

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button({
                onAction(PlanAction.SavePlan)
                onSave()
            }) {
                Text("Save plan")
            }

            Button({
                onAction(PlanAction.DiscardPlan)
                onDiscard()
            }) {
                Text("Discard plan")
            }
        }
    }
}