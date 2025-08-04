package io.github.jakubherr.gitfit.previews

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.presentation.planning.EditProgressionScreen
import io.github.jakubherr.gitfit.presentation.planning.PlanCreationScreen
import io.github.jakubherr.gitfit.presentation.planning.PlanDetailScreen
import io.github.jakubherr.gitfit.presentation.planning.PlanWorkoutCreationScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview
@Composable
private fun PlanDetailScreenPreview() {
    GitFitTheme {
        Surface {
            PlanDetailScreen(
                plan = mockPlan,
                isPredefined = false,
            )
        }
    }
}

@Preview
@Composable
private fun PlanWorkoutDetailPreview() {
    GitFitTheme {
        Surface {
            PlanWorkoutCreationScreen(
                workoutPlan = mockWorkoutPlan,
            )
        }
    }
}

@Preview
@Composable
private fun EditProgressionPreview() {
    GitFitTheme {
        Surface {
            EditProgressionScreen(
                block = mockBlock,
            )
        }
    }
}

@Preview
@Composable
private fun PlanCreationScreenPreview() {
    GitFitTheme {
        Surface {
            PlanCreationScreen(
                plan = mockPlan,
            )
        }
    }
}

@Preview
@Composable
private fun PlanWorkoutCreationPreview() {
    GitFitTheme {
        Surface {
            PlanWorkoutCreationScreen(
                mockWorkoutPlan,
            )
        }
    }
}
