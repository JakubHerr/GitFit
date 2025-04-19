package instrumented

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.test.waitUntilExactlyOneExists
import io.github.jakubherr.gitfit.presentation.App
import kotlin.test.Test

class PlanTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun planTest() = runComposeUiTest {
        setContent {
            App()
        }

        login()

        onNodeWithText("Plány").performClick()
        waitForIdle()

        copyPredefinedPlan()

        onNodeWithTag("UserPlanListItem").performClick()

        editPlan()

        deletePlan()

        createNewPlan()

        onNodeWithTag("UserPlanListItem").performClick()

        deletePlan()

        logout()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.copyPredefinedPlan() {
        waitUntilAtLeastOneExists(
            hasTestTag("PredefinedPlanListItem"),
            timeoutMillis = 3000
        )
        onAllNodesWithTag("PredefinedPlanListItem").onFirst().performClick()
        waitForIdle()
        waitUntilExactlyOneExists(hasTestTag("CopyDefaultPlan"))
        onNodeWithTag("CopyDefaultPlan").performClick()

        waitUntilExactlyOneExists(
            hasTestTag("UserPlanListItem"),
            timeoutMillis = 3000
        )
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.editPlan() {
        waitUntilExactlyOneExists(hasTestTag("EditPlanButton"))
        onNodeWithTag("EditPlanButton").performClick()

        // Rename plan
        waitUntilExactlyOneExists(hasTestTag("PlanNameInput"))
        onNodeWithTag("PlanNameInput").apply {
            performTextClearance()
            performTextInput("Název plánu")
        }

        // Delete workout day
        onAllNodesWithTag("WorkoutPlanListItemAction").onFirst().performClick()
        waitForIdle()

        // Rename workout day
        onAllNodesWithTag("WorkoutPlanListItem").onFirst().performClick()
        waitUntilExactlyOneExists(hasTestTag("WorkoutPlanNameInput"))
        onNodeWithTag("WorkoutPlanNameInput").apply {
            performTextClearance()
            performTextInput("Název dne")
        }
        waitForIdle()

        // Delete set from exercise
        onAllNodesWithTag("WorkoutPlanDeleteSeries").onFirst().performClick()
        waitForIdle()

        // Delete exercise from workout
        onAllNodesWithTag("PlanExerciseDropdown").onFirst().performClick()
        onNodeWithTag("PlanDeleteExercise").performClick()

        // confirm workout plan changes
        onNodeWithTag("ConfirmWorkoutPlan").performClick()

        // confirm plan changes
        waitUntilExactlyOneExists(hasTestTag("SavePlan"))
        onNodeWithTag("SavePlan").performClick()
        waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.createNewPlan() {
        // create new plan
        onNodeWithTag("CreateNewPlanButton").performClick()

        // add new day
        waitUntilExactlyOneExists(hasTestTag("PlanAddWorkoutDay"))
        onNodeWithTag("PlanAddWorkoutDay").performClick()

        waitUntilExactlyOneExists(hasTestTag("WorkoutPlanListItem"))
        onNodeWithTag("WorkoutPlanListItem").performClick()

        // add new exercise
        waitUntilExactlyOneExists(hasTestTag("WorkoutPlanAddExercise"))
        onNodeWithTag("WorkoutPlanAddExercise").performClick()
        waitForIdle()
        waitUntilDoesNotExist(
            hasTestTag("EmptyExerciseList"),
            timeoutMillis = 3000
        )
        onNodeWithText("Bench Press").performClick()

        // add new valid series
        waitUntilExactlyOneExists(hasTestTag("AddSeriesButton"))
        onNodeWithTag("AddSeriesButton").performClick()
        onNodeWithTag("WorkoutPlanWeightInput").performTextInput("12.34")
        onNodeWithTag("WorkoutPlanRepsInput").performTextInput("56")
        waitForIdle()

        // save workout day
        onNodeWithTag("ConfirmWorkoutPlan").performClick()

        // save plan
        waitUntilExactlyOneExists(hasTestTag("SavePlan"))
        onNodeWithTag("SavePlan").performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.deletePlan() {
        waitUntilExactlyOneExists(hasTestTag("DeletePlanButton"))
        onNodeWithTag("DeletePlanButton").performClick()
        waitForIdle()
        onNodeWithTag("ConfirmDialogButton").performClick()
        waitUntilDoesNotExist(
            hasTestTag("UserPlanListItem"),
            timeoutMillis = 3000L
        )
    }
}