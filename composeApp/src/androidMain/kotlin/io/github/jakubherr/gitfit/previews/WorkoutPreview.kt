package io.github.jakubherr.gitfit.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.presentation.workout.WorkoutInProgressScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160")
@Composable
private fun WorkoutScreenPreview() {
    GitFitTheme {
        WorkoutInProgressScreen(mockWorkout) {}
    }
}