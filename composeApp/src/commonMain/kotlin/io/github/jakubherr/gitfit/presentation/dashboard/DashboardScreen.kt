package io.github.jakubherr.gitfit.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.planned_workouts
import gitfit.composeapp.generated.resources.resume_workout
import gitfit.composeapp.generated.resources.start_unplanned_workout
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.shared.WorkoutListItem
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun DashboardScreenRoot(
    vm: WorkoutViewModel,
    modifier: Modifier = Modifier,
    onAction: (DashboardAction) -> Unit,
) {
    val planned by vm.plannedWorkouts.collectAsStateWithLifecycle()
    val current by vm.currentWorkout.collectAsStateWithLifecycle()

    DashboardScreen(
        plannedWorkouts = planned,
        currentWorkout = current,
    ) { action ->
        when (action) {
            is DashboardAction.UnplannedWorkoutClick -> vm.onAction(WorkoutAction.StartNewWorkout)
            // is DashboardAction.PlannedWorkoutClick -> vm.onAction(WorkoutAction.StartPlannedWorkout(action.workoutId))
            else -> {}
        }
        onAction(action)
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    plannedWorkouts: List<Workout> = emptyList(),
    currentWorkout: Workout? = null,
    onAction: (DashboardAction) -> Unit = { },
) {
    val scrollState = rememberScrollState()

    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState),
    ) {
        WorkoutSection(plannedWorkouts, onAction)
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
    }
}

@Composable
fun WorkoutSection(
    workouts: List<Workout>,
    onAction: (DashboardAction) -> Unit = {},
) {
    WorkoutListHeader()
    LazyRow(
        contentPadding = PaddingValues(16.dp),
    ) {
        items(workouts) { workout ->
            WorkoutListItem(workout) { onAction(DashboardAction.PlannedWorkoutClick(workout.id)) }
        }
    }
}

@Composable
fun WorkoutListHeader(modifier: Modifier = Modifier) {
    Row {
        Text(stringResource(Res.string.planned_workouts))
    }
}

