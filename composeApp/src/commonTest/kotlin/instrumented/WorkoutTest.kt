package instrumented

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.github.jakubherr.gitfit.presentation.App
import kotlin.test.Test

class WorkoutTest {
    // This test validates that user can start or resume a workout from the dashboard
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun WorkoutTest() = runComposeUiTest {
        setContent {
            App()
        }

        login()

        // start new or resume current workout
        onNodeWithTag("StartWorkoutButton").performClick()
        waitForIdle()

        onNodeWithTag("WorkoutAddExercise").performClick()
        waitForIdle()
    }
}