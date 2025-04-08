package io.github.jakubherr.gitfit

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.mockExercise
import io.github.jakubherr.gitfit.domain.model.mockWorkout
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreen
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseCreateScreen
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreen
import io.github.jakubherr.gitfit.presentation.workout.WorkoutInProgressScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview
@Composable
private fun DashboardScreenPreview() {
    GitFitTheme {
        Surface {
            DashboardScreen { }
        }
    }
}

@Preview
@Composable
private fun WorkoutScreenPreview() {
    WorkoutInProgressScreen(mockWorkout) {}
}

@Preview
@Composable
private fun ExerciseListScreenPreview() {
    GitFitTheme {
        Surface {
            ExerciseListScreen(
                // emptyList(),
                listOf(mockExercise, mockExercise, mockExercise),
            ) { }
        }
    }
}

@Preview
@Composable
private fun CreateExerciseScreenPreview() {
    GitFitTheme {
        Surface {
            ExerciseCreateScreen()
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailScreenPreview() {
    GitFitTheme {
        Surface {
            // ExerciseDetailScreen()
        }
    }
}
