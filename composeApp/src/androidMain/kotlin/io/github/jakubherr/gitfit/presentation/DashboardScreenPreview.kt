package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview
@Composable
private fun DashboardScreenPreview() {
    val currentWorkout =
        Workout(
            id = "mockId",
            blocks = emptyList(),
        )

    GitFitTheme {
        DashboardScreen(
            currentWorkout = currentWorkout,
        )
    }
}
