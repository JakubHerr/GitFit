package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.shared.WorkoutListItem

@Composable
fun WorkoutListScreen(
    workoutList: List<Workout>,
    modifier: Modifier = Modifier,
    onWorkoutSelected: (Workout) -> Unit,
) {
    Column(
        modifier.padding(16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(workoutList) { workout ->
                WorkoutListItem(workout) { onWorkoutSelected(workout) }
            }
        }
    }
}