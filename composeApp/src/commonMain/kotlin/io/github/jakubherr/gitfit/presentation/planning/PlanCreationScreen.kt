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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_workout_day
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.discard_changes
import gitfit.composeapp.generated.resources.discard_plan_changes
import gitfit.composeapp.generated.resources.discard_plan_changes_explanation
import gitfit.composeapp.generated.resources.plan_name
import gitfit.composeapp.generated.resources.save
import gitfit.composeapp.generated.resources.workout_days
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import io.github.jakubherr.gitfit.presentation.shared.OnBackPress
import io.github.jakubherr.gitfit.presentation.shared.StringInputField
import io.github.jakubherr.gitfit.presentation.shared.WorkoutPlanListItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlanCreationScreen(
    plan: Plan,
    modifier: Modifier = Modifier,
    onAction: (PlanAction) -> Unit = {},
    onWorkoutSelected: (Int) -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }
    OnBackPress { showDialog = true }
    if (showDialog) {
        ConfirmationDialog(
            title = stringResource(Res.string.discard_plan_changes),
            text = stringResource(Res.string.discard_plan_changes_explanation),
            confirmText = stringResource(Res.string.discard_changes),
            dismissText = stringResource(Res.string.cancel),
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                onAction(PlanAction.DiscardPlan)
            },
        )
    }

    Column(
        modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StringInputField(
            value = plan.name,
            onValueChange = { onAction(PlanAction.RenamePlan(it)) },
            maxLength = 20,
            label = { Text(stringResource(Res.string.plan_name)) },
            placeholder = { Text(plan.name, Modifier.alpha(0.5f)) },
        )

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Res.string.workout_days))

            Button({
                onAction(PlanAction.AddWorkout(WorkoutPlan.Empty(plan.workoutPlans.size)))
            }) {
                Text(stringResource(Res.string.add_workout_day))
            }
        }

        HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 16.dp))

        LazyColumn(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(plan.workoutPlans) { workout ->
                WorkoutPlanListItem(
                    workout,
                    onItemClicked = { onWorkoutSelected(workout.idx) },
                    onActionClicked = { onAction(PlanAction.DeleteWorkout(workout)) },
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Button({ onAction(PlanAction.SavePlan) }) { Text(stringResource(Res.string.save)) }

            Button({ showDialog = true }) { Text(stringResource(Res.string.discard_changes)) }
        }
    }
}
