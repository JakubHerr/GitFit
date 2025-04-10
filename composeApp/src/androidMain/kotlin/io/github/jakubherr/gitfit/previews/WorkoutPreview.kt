package io.github.jakubherr.gitfit.previews

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.presentation.workout.WorkoutDetailScreen
import io.github.jakubherr.gitfit.presentation.workout.WorkoutInProgressScreen
import io.github.jakubherr.gitfit.presentation.workout.WorkoutListScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160")
@Composable
private fun WorkoutInProgressScreenPreview() {
    GitFitTheme {
        WorkoutInProgressScreen(mockWorkout) {}
    }
}

@Preview
@Composable
private fun WorkoutDetailScreenPreview() {
    GitFitTheme {
        Surface {
            WorkoutDetailScreen(
                mockWorkout
            )
        }
    }
}

@Preview
@Composable
private fun WorkoutListScreenPreview() {
    GitFitTheme {
        Surface {
            WorkoutListScreen(
                workoutList = listOf(mockWorkout, mockWorkout, mockWorkout)
            ) { }
        }
    }
}