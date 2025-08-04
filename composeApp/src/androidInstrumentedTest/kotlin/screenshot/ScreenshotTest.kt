package screenshot

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.jakubherr.gitfit.presentation.App
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(AndroidJUnit4::class)
class ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val localeTestRule = LocaleTestRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun takeScreenshot() {
        composeTestRule.setContent {
            App()
        }

        with(composeTestRule) {
            onNodeWithTag("EmailInput").performTextInput("uitest@test.test")
            waitForIdle()

            onNodeWithTag("PasswordInput").performTextInput("uitestpassword")
            waitForIdle()

            onNodeWithTag("LoginButton").performClick()

            waitUntilDoesNotExist(
                hasTestTag("AuthProgressIndicator"),
                timeoutMillis = 3000,
            )

            onNodeWithTag("SkipVerificationButton").performClick()
            waitForIdle()

            Screengrab.screenshot("dashboard")

            // TODO add full screenshot script
        }
    }
}
