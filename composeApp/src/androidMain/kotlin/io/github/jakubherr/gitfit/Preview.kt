package io.github.jakubherr.gitfit

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.mockWorkout
import io.github.jakubherr.gitfit.presentation.WorkoutScreen

@Preview
@Composable
private fun WorkoutScreenPreview() {
    WorkoutScreen(mockWorkout)
}