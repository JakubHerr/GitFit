package screenshot

import androidx.compose.ui.test.junit4.createComposeRule
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

    @Test
    fun takeScreenshot() {
        composeTestRule.setContent {
            // Your Compose UI
            App()
        }

        Screengrab.screenshot("home_screen")
    }
}
