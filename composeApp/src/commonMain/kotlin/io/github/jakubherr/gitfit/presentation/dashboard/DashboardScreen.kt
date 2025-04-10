package io.github.jakubherr.gitfit.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.resume_workout
import gitfit.composeapp.generated.resources.start_unplanned_workout
import gitfit.composeapp.generated.resources.training_plans
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.planning.PlanningViewModel
import io.github.jakubherr.gitfit.presentation.shared.PlanLazyColumn
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun DashboardScreenRoot(
    vm: WorkoutViewModel,
    planVM: PlanningViewModel,
    modifier: Modifier = Modifier,
    onAction: (DashboardAction) -> Unit,
    onPlanSelected: (Plan) -> Unit,
) {
    val current by vm.currentWorkout.collectAsStateWithLifecycle()
    val plans by planVM.userPlans.collectAsStateWithLifecycle(emptyList())

    DashboardScreen(
        currentWorkout = current,
        userPlans = plans,
        onAction = { action ->
            if (action is DashboardAction.UnplannedWorkoutClick) vm.onAction(WorkoutAction.StartNewWorkout)
            onAction(action)
        },
        onPlanSelected = { onPlanSelected(it) },
    )
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    userPlans: List<Plan> = emptyList(),
    currentWorkout: Workout? = null,
    onAction: (DashboardAction) -> Unit = { },
    onPlanSelected: (Plan) -> Unit = {},
) {
    Surface {
        Column(
            Modifier.padding(16.dp).fillMaxSize(),
        ) {
            PlanLazyColumn(
                userPlans,
                stringResource(Res.string.training_plans),
                modifier = Modifier.weight(1.0f),
                onPlanSelected = { onPlanSelected(it) },
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentWorkout == null) {
                    Button({ onAction(DashboardAction.UnplannedWorkoutClick) }) {
                        Text(stringResource(Res.string.start_unplanned_workout))
                    }
                } else {
                    Button({ onAction(DashboardAction.ResumeWorkoutClick) }) {
                        Text(stringResource(Res.string.resume_workout))
                    }
                }
            }
        }
    }
}
