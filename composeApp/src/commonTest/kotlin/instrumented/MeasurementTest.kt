package instrumented

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import io.github.jakubherr.gitfit.presentation.App
import io.github.jakubherr.gitfit.presentation.measurement.MeasurementType
import kotlin.test.Test

class MeasurementTest {

    // add todays measurement
    // edit todays measurement
    // check history for measurement record
    // delete measurement record
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun measurementTest() = runComposeUiTest {
        setContent {
            App()
        }

        login()
        createNewMeasurement()
        waitUntilExactlyOneExists(hasText("Upravit dnešní měření"))

        onNodeWithText("Upravit dnešní měření").performClick()
        waitForIdle()
        addMeasurementValues("32.1")
        onNodeWithTag("SaveMeasurementButton").performClick()
        waitForIdle()

        onNodeWithText("Zobrazit vše").performClick()
        waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.createNewMeasurement() {
        onNodeWithText("Měření").performClick()
        waitUntilExactlyOneExists(hasText("Přidat dnešní měření"))
        onNodeWithText("Přidat dnešní měření").performClick()
        waitForIdle()
        addMeasurementValues("12.3")
        onNodeWithTag("SaveMeasurementButton").performClick()
    }



    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.addMeasurementValues(value: String) {
        MeasurementType.entries.forEachIndexed { idx, _ ->
            val tag = "MeasurementInput$idx"
            onNodeWithTag("MeasurementLazyList").performScrollToNode(hasTestTag(tag))
            onNodeWithTag(tag).performTextClearance()
            onNodeWithTag(tag).performTextInput(value)
        }
        waitForIdle()
    }
}