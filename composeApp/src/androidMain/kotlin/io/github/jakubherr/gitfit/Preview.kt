package io.github.jakubherr.gitfit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.mockExercise
import io.github.jakubherr.gitfit.domain.model.mockWorkout
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreen
import io.github.jakubherr.gitfit.presentation.exercise.CreateExerciseScreen
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseDetailScreen
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreen
import io.github.jakubherr.gitfit.presentation.workout.WorkoutScreen

@Preview
@Composable
private fun DashboardScreenPreview() {
    MaterialTheme {
        Surface {
            DashboardScreen { }
        }
    }
}

@Preview
@Composable
private fun WorkoutScreenPreview() {
    WorkoutScreen(mockWorkout) {}
}

@Preview
@Composable
private fun ExerciseListScreenPreview() {
    MaterialTheme {
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
    MaterialTheme {
        Surface {
            CreateExerciseScreen()
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailScreenPreview() {
    MaterialTheme {
        Surface {
            ExerciseDetailScreen()
        }
    }
}
