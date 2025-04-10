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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_to_your_plans
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.confirm
import gitfit.composeapp.generated.resources.delete_plan
import gitfit.composeapp.generated.resources.plan_deletion_expalantion
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import io.github.jakubherr.gitfit.presentation.shared.WorkoutPlanListItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlanDetailScreen(
    plan: Plan,
    isPredefined: Boolean,
    modifier: Modifier = Modifier,
    onWorkoutSelected: (WorkoutPlan) -> Unit = {},
    onAction: (PlanAction) -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ConfirmationDialog(
            title = stringResource(Res.string.delete_plan),
            text = stringResource(Res.string.plan_deletion_expalantion),
            confirmText = stringResource(Res.string.confirm),
            dismissText = stringResource(Res.string.cancel),
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                onAction(PlanAction.DeletePlan(plan.id))
            },
        )
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(plan.name, fontWeight = FontWeight.Bold)

            Row {
                if (isPredefined) {
                    Button(
                        { onAction(PlanAction.CopyDefaultPlan(plan)) }
                    ) {
                        Text(stringResource(Res.string.add_to_your_plans))
                    }
                } else {
                    IconButton({ onAction(PlanAction.EditPlan(plan)) }) {
                        Icon(Icons.Default.Edit, "")
                    }

                    IconButton(
                        onClick = { showDialog = true },
                    ) {
                        Icon(Icons.Default.Delete, "", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(plan.workoutPlans) { workout ->
                WorkoutPlanListItem(
                    workout,
                    onActionClicked = { onWorkoutSelected(workout) },
                    actionSlot = {
                        if (!isPredefined) Icon(Icons.Default.PlayArrow, "", tint = MaterialTheme.colorScheme.primary)
                    },
                )
            }
        }
    }

    // nice to have: user has the ability to schedule a workout for a certain date
}
