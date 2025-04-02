package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.shared.WorkoutBlockItem

@Composable
fun WorkoutDetailScreen(
    workout: Workout,
    modifier: Modifier = Modifier
) {
    workout.blocks.forEach { block ->
        WorkoutBlockItem(block)
    }
}