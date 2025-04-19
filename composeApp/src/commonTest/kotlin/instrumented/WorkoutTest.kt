package instrumented

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.test.waitUntilExactlyOneExists
import io.github.jakubherr.gitfit.presentation.App
import kotlin.test.Test

class WorkoutTest {
    // This test validates that user can:
    //  start a workout from dashboard
    //  resume a workout in progress from dashboard
    //  add new exercise from a list
    //  add new series, enter valid values and toggle checkbox
    //  add new series, enter invalid values and not toggle checkbox
    //  remove last series
    //  remove exercise from workout
    //  delete workout in progress and confirm with dialog
    //  save workout with valid values
    //  delete workout record from history
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun WorkoutTest() = runComposeUiTest {
        setContent {
            App()
        }

        login()

        startNewWorkout()
        addExerciseToWorkout("Bench Press")
        addNewInvalidSeries()
        deleteLastSeries()
        removeExerciseFromWorkout()
        deleteWorkoutInProgress()

        startNewWorkout()
        addExerciseToWorkout("Bench Press")
        addNewValidSeries()

        navigateBack()
        resumeWorkout()

        // save workout
        onNodeWithTag("SaveWorkoutInProgress").performClick()
        waitUntilExactlyOneExists(
            hasText("Spustit neplánovaný trénink"),
            timeoutMillis = 3000
        )

        navigateToWorkoutRecord()

        // delete and confirm deletion
        onNodeWithTag("DeleteWorkoutRecordButton").performClick()
        onNodeWithTag("ConfirmDialogButton").performClick()

        // wait for record list to be empty
        waitUntilDoesNotExist(
            hasTestTag("WorkoutListItem"),
            timeoutMillis = 3000
        )

        navigateBack()
        logout()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.startNewWorkout() {
        onNodeWithTag("StartWorkoutButton").performClick()
        waitUntilExactlyOneExists(
            hasTestTag("WorkoutAddExercise"),
            timeoutMillis = 3000
        )
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.resumeWorkout() {
        waitUntilExactlyOneExists(
            hasText("Pokračovat v záznamu"),
            timeoutMillis = 3000
        )
        onNodeWithText("Pokračovat v záznamu").performClick()
        waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.addExerciseToWorkout(exerciseName: String) {
        // click on add new exercise
        onNodeWithTag("WorkoutAddExercise").performClick()
        waitForIdle()
        // wait for exercise list to load and add some exercise
        waitUntilDoesNotExist(
            hasTestTag("EmptyExerciseList"),
            timeoutMillis = 3000
        )
        onNodeWithText(exerciseName).performClick()
        waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.addNewValidSeries() {
        onNodeWithText("Přidat sérii").performClick()
        waitUntilExactlyOneExists(hasTestTag("WorkoutWeightInput"))
        onNodeWithTag("WorkoutWeightInput").performTextInput("80")
        onNodeWithTag("WorkoutRepsInput").performTextInput("10")
        waitForIdle()
        onNodeWithTag("WorkoutSeriesCheckbox")
            .performClick()
            .assertIsEnabled()
        waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.addNewInvalidSeries() {
        waitUntilDoesNotExist(hasTestTag("WorkoutWeightInput"))
        onNodeWithText("Přidat sérii").performClick()
        waitUntilExactlyOneExists(hasTestTag("WorkoutWeightInput"))
        onNodeWithTag("WorkoutWeightInput").performTextInput("-1.2")
        onNodeWithTag("WorkoutRepsInput").performTextInput("abc")
        waitForIdle()
        onNodeWithTag("WorkoutSeriesCheckbox").assertIsNotEnabled()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.deleteLastSeries() {
        onNodeWithTag("WorkoutExerciseDropdown").performClick()
        waitForIdle()
        onNodeWithTag("WorkoutDeleteLastSet").performClick()
        waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.removeExerciseFromWorkout() {
        onNodeWithTag("WorkoutExerciseDropdown").performClick()
        onNodeWithTag("WorkoutDeleteExercise").performClick()
        waitUntilDoesNotExist(hasTestTag("WorkoutBlockItem"))
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.deleteWorkoutInProgress() {
        // click on delete workout and confirm deletion in dialog
        onNodeWithTag("DeleteWorkoutInProgress").performClick()
        waitUntilExactlyOneExists(hasTestTag("ConfirmationDialog"))
        onNodeWithTag("ConfirmDialogButton").performClick()
        waitForIdle()

        // check that there is no workout in progress
        waitUntilExactlyOneExists(
            hasText("Spustit neplánovaný trénink"),
            timeoutMillis = 3000
        )
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.navigateToWorkoutRecord() {
        // go to history, click on browse workout records
        onNodeWithText("Historie").performClick()
        onNodeWithTag("BrowseWorkoutHistoryButton").performClick()

        // wait for record to show up
        waitUntilExactlyOneExists(
            hasTestTag("WorkoutListItem"),
            timeoutMillis = 3000
        )

        // click on it
        onNodeWithTag("WorkoutListItem").performClick()
        waitForIdle()
    }
}
