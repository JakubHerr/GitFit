package instrumented

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.test.waitUntilExactlyOneExists
import io.github.jakubherr.gitfit.presentation.App
import kotlin.test.Test

// TODO watch https://www.youtube.com/watch?v=otRqF7SQrz4
// https://proandroiddev.com/write-unit-tests-and-ui-tests-in-your-kotlin-multiplatform-app-472c27625b5a

// https://medium.com/@vptarasov/unit-testing-in-kotlin-multiplatform-fb3ca7d5c869
class WorkoutTest {
    // This test validates that user can:
    //  start or resume a workout from the dashboard
    //  add new exercise for a list
    //  confirm workout deletion with confirmation dialog
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

        waitUntilDoesNotExist(
            hasTestTag("EmptyExerciseList"),
            timeoutMillis = 3000
        )

        onNodeWithText("Bench Press").performClick()
        waitForIdle()

        // click on delete workout
        onNodeWithTag("DeleteWorkoutInProgress").performClick()
        waitUntilExactlyOneExists(
            hasTestTag("ConfirmationDialog")
        )

        // confirm dialog
        onNodeWithTag("ConfirmDialogButton").performClick()
        waitForIdle()

        waitUntilExactlyOneExists(
            hasText("Spustit neplánovaný trénink"),
            timeoutMillis = 3000
        )
    }
}