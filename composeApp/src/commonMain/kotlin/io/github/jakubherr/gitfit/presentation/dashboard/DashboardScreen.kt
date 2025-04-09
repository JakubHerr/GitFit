package io.github.jakubherr.gitfit.presentation.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import gitfit.composeapp.generated.resources.resume_workout
import gitfit.composeapp.generated.resources.start_unplanned_workout
import gitfit.composeapp.generated.resources.training_plans
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.planning.PlanListItem
import io.github.jakubherr.gitfit.presentation.planning.PlanningViewModel
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
        onPlanSelected = { onPlanSelected(it) }
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
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

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            WorkoutPlanSection(
                userPlans,
                onClick = { onPlanSelected(it) }
            )
        }
    }
}

@Composable
fun WorkoutPlanSection(
    workouts: List<Plan>,
    onClick: (Plan) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp).clickable { expanded = !expanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(Res.string.training_plans))

        val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
        Icon(icon, "")
    }
    Spacer(Modifier.height(8.dp))
    LazyColumn(
        modifier = Modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(workouts) { plan ->
            if (expanded) PlanListItem(plan) { onClick(it) }
        }
    }
}
