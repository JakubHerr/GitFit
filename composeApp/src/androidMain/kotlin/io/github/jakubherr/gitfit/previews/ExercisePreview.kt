package io.github.jakubherr.gitfit.previews

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseCreateScreen
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseDetailScreen
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreen
import io.github.jakubherr.gitfit.presentation.graph.ExerciseMetric
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme
import io.github.koalaplot.core.xygraph.DefaultPoint

@Preview
@Composable
private fun ExerciseListScreenPreview() {
    GitFitTheme {
        Surface {
            ExerciseListScreen(
                listOf(mockExercise, mockExercise, mockExercise),
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseCreateScreenPreview() {
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
            // note: graph does not show values in preview
            ExerciseDetailScreen(
                mockExercise,
                graphData = listOf(
                    DefaultPoint("2024-04-09", 70)
                ),
                selectedMetric = ExerciseMetric.HEAVIEST_WEIGHT,
            )
        }
    }
}
