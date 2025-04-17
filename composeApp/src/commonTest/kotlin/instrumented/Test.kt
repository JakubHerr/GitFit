package instrumented

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilDoesNotExist
import io.github.jakubherr.gitfit.presentation.App
import kotlin.test.Test

class NavigationTest {

    // this test validates use cases F02 and F06 with real database on a testing account
    // It also navigates through all top level destinations in the app
    // this test assumes that it is run on a real device with internet access with Czech localization
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun navigationTest() = runComposeUiTest {
        setContent {
            App()
        }

        onNodeWithText("Zapomenuté heslo").performClick()
        waitForIdle()

        onNodeWithContentDescription("Navigovat zpět").performClick()
        waitForIdle()

        onNodeWithText("Email").performTextInput("uitest@test.test")
        waitForIdle()

        onNodeWithText("Heslo").performTextInput("uitestpassword")
        waitForIdle()

        onNodeWithText("Přihlásit se").performClick()

        waitUntilDoesNotExist(
            hasTestTag("AuthProgressIndicator"),
            timeoutMillis = 3000
        )

        onNodeWithText("Přeskočit ověření").performClick()
        waitForIdle()

        onNodeWithText("Plány").performClick()
        waitForIdle()

        onNodeWithText("Měření").performClick()
        waitForIdle()

        onNodeWithText("Historie").performClick()
        waitForIdle()

        onNodeWithText("Nastavení").performClick()
        waitForIdle()

        onNodeWithText("Odhlásit se").performClick()

        waitUntilDoesNotExist(
            hasTestTag("AuthProgressIndicator"),
            timeoutMillis = 3000
        )
    }
}
