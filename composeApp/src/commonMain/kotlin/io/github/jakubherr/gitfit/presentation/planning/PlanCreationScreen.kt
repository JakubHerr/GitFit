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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.shared.StringInputField
import io.github.jakubherr.gitfit.presentation.shared.WorkoutListItem

@Composable
fun PlanCreationScreen(
    plan: Plan,
    modifier: Modifier = Modifier,
    onAction: (PlanAction) -> Unit = {},
    onWorkoutSelected: (Int) -> Unit = {},
) {
    Column(
        modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StringInputField(
            value = plan.name,
            onValueChange = { onAction(PlanAction.RenamePlan(it)) },
            maxLength = 20,
            label = { Text("Plan name") }
        )

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Workout days")

            Button({
                val workout = WorkoutPlan(
                    "New workout",
                    plan.workoutPlans.size,
                    emptyList(),
                )
                onAction(PlanAction.AddWorkout(workout))
            }) {
                Text("Add workout day")
            }
        }

        HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(plan.workoutPlans) { workout ->
                WorkoutListItem(
                    workout,
                    onItemClicked = { onWorkoutSelected(workout.idx) },
                    onActionClicked = { onAction(PlanAction.DeleteWorkout(workout))}
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().weight(1.0f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Button({ onAction(PlanAction.SavePlan) }) { Text("Save plan") }

            Button({ onAction(PlanAction.DiscardPlan) }) { Text("Discard changes") }
        }
    }
}