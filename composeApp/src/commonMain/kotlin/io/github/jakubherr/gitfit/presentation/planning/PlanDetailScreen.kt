package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan

@Composable
fun PlanDetailScreen(
    plan: Plan,
    modifier: Modifier = Modifier,
    onWorkoutSelected: (WorkoutPlan) -> Unit = {},
    onPlanSelected: () -> Unit = {}, // TODO User can add a predefined plan to his plans, where he can edit it etc.
    onAction: (PlanAction) -> Unit = {},
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(plan.name.ifBlank { "Unnamed plan" })

            Row {
                IconButton({ onAction(PlanAction.EditPlan(plan)) }) {
                    Icon(Icons.Default.Edit, "")
                }
                // TODO: "are you sure?" dialog
                IconButton({ onAction(PlanAction.DeletePlan(plan.id)) }) {
                    Icon(Icons.Default.Delete, "")
                }
            }

        }

        LazyColumn {
            items(plan.workoutPlans) { workout ->
                WorkoutListItem(
                    workout,
                    onActionClicked = { onWorkoutSelected(workout) },
                    actionSlot = { Icon(Icons.Default.PlayArrow, "") }
                )
            }
        }
    }
    
    // nice to have: user has the ability to schedule a workout for a certain date
}