package instrumented

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilDoesNotExist
import io.github.jakubherr.gitfit.presentation.App
import kotlin.test.Test

class ExerciseTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun exerciseTest() =
        runComposeUiTest {
            setContent {
                App()
            }

            login()

            onNodeWithText("Historie").performClick()
            onNodeWithTag("BrowseExerciseHistoryButton").performClick()

            waitUntilDoesNotExist(
                hasTestTag("EmptyExerciseList"),
                timeoutMillis = 3000,
            )

            onNodeWithText("Bench Press").performClick()
            navigateBack()

            // create custom exercise
            onNodeWithTag("CreateExerciseButton").performClick()
            onNodeWithTag("ExerciseNameInput").performTextInput("Custom exercise")
            onNodeWithTag("SaveExerciseButton").performClick()

            // search  for custom exercise
            onNodeWithTag("ExerciseSearchBar").performTextInput("Custom")
            waitForIdle()
            onNodeWithText("Custom exercise").performClick()

            // delete custom exercise
            onNodeWithTag("DeleteExerciseButton").performClick()
            onNodeWithTag("ConfirmDialogButton").performClick()

            navigateBack()
            logout()
        }
}
