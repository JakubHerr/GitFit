package io.github.jakubherr.gitfit.presentation.dashboard

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.planned_workouts
import gitfit.composeapp.generated.resources.start_unplanned_workout
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.mockWorkout
import io.github.jakubherr.gitfit.presentation.auth.AuthAction
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreenRoot(
    // vm: AuthViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onAction: (DashboardAction) -> Unit,
) {
    // val state = vm.state.collectAsStateWithLifecycle()

    DashboardScreen(onAction = onAction)
}


// TODO Scrollable list with composables
//  show section with data and meaurements,
//      - maybe add some customization options
//  show section with planned workouts
//      - option to quick start planned workout, plan a new workout etc.
//  show section with exercises
@Composable
fun DashboardScreen(
    // state: AuthState,
    modifier: Modifier = Modifier,
    onAction: (DashboardAction) -> Unit = { }
) {

    val scrollState = rememberScrollState()
    val mockWorkouts = listOf(mockWorkout, mockWorkout)

    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState),
    ) {
        WorkoutSection(mockWorkouts, onAction)
    }
}

// TODO show today's plan and this week's plan
@Composable
fun WorkoutSection(
    workouts: List<Workout>,
    onAction: (DashboardAction) -> Unit = {},
) {
    WorkoutListHeader()
    LazyRow(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(workouts) { workout ->
            WorkoutListItem(workout) { onAction(DashboardAction.PlannedWorkoutClick(workout.id)) }
        }
    }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button({ onAction(DashboardAction.UnplannedWorkoutClick) }) {
            Text(stringResource(Res.string.start_unplanned_workout))
        }
    }
}

@Composable
fun WorkoutListHeader(modifier: Modifier = Modifier) {
    Row {
        Text(stringResource(Res.string.planned_workouts))
    }
}

@Composable
fun WorkoutListItem(
    workout: Workout,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier.clickable { onClick() }
    ) {
        Column {
            Text(workout.id)
            Text(workout.date.toString())



            workout.blocks.forEach { block ->
                Text(block.exercise.name)
            }
        }
    }
}