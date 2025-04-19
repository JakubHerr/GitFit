package instrumented

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.test.waitUntilExactlyOneExists
import io.github.jakubherr.gitfit.presentation.App
import kotlin.test.AfterTest
import kotlin.test.Test

class WorkoutTest {
    // This test validates that user can:
    //  start a workout from the dashboard
    //  add new exercise from a list
    //  add new series, enter valid values and toggle checkbox
    //  remove last series
    //  add new series with invalid values and not toggle checkbox
    //  remove exercise from workout
    //  confirm workout deletion with confirmation dialog
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun WorkoutTest() = runComposeUiTest {
        setContent {
            App()
        }

        login()

        // start new workout
        onNodeWithTag("StartWorkoutButton").performClick()
        waitUntilExactlyOneExists(
            hasTestTag("WorkoutAddExercise"),
            timeoutMillis = 3000
        )

        // click on add new exercise
        onNodeWithTag("WorkoutAddExercise").performClick()
        waitForIdle()

        // wait for exercise list to load and add some exercise
        waitUntilDoesNotExist(
            hasTestTag("EmptyExerciseList"),
            timeoutMillis = 3000
        )
        onNodeWithText("Bench Press").performClick()
        waitForIdle()

        // add new series to exercise
        onNodeWithText("Přidat sérii").performClick()

        // input and check valid series
        waitUntilExactlyOneExists(hasTestTag("WorkoutWeightInput"))
        onNodeWithTag("WorkoutWeightInput").performTextInput("80")
        onNodeWithTag("WorkoutRepsInput").performTextInput("10")
        waitForIdle()
        onNodeWithTag("WorkoutSeriesCheckbox")
            .assertIsEnabled()
            .performClick()

        // delete last series
        onNodeWithTag("WorkoutExerciseDropdown").performClick()
        waitForIdle()
        onNodeWithTag("WorkoutDeleteLastSet").performClick()
        waitForIdle()

        // input and try to check invalid series
        waitUntilDoesNotExist(hasTestTag("WorkoutWeightInput"))
        onNodeWithText("Přidat sérii").performClick()
        waitUntilExactlyOneExists(hasTestTag("WorkoutWeightInput"))
        onNodeWithTag("WorkoutWeightInput").performTextInput("-1.2")
        onNodeWithTag("WorkoutRepsInput").performTextInput("abc")
        waitForIdle()
        onNodeWithTag("WorkoutSeriesCheckbox").assertIsNotEnabled()

        // remove exercise from workout
        onNodeWithTag("WorkoutExerciseDropdown").performClick()
        waitForIdle()
        onNodeWithTag("WorkoutDeleteExercise").performClick()

        waitUntilDoesNotExist(hasTestTag("WorkoutBlockItem"))

        // click on delete workout and confirm deletion in dialog
        onNodeWithTag("DeleteWorkoutInProgress").performClick()
        waitUntilExactlyOneExists(
            hasTestTag("ConfirmationDialog")
        )
        onNodeWithTag("ConfirmDialogButton").performClick()
        waitForIdle()

        // check that there is no workout in progress
        waitUntilExactlyOneExists(
            hasText("Spustit neplánovaný trénink"),
            timeoutMillis = 3000
        )

        logout()
    }
}
