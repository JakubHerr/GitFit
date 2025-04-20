package instrumented

import androidx.compose.ui.test.ComposeUiTest
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

        navigateBack()

        login()

        onNodeWithText("Plány").performClick()
        waitForIdle()

        onNodeWithText("Měření").performClick()
        waitForIdle()

        onNodeWithText("Historie").performClick()
        waitForIdle()

        logout()
    }
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.login() {
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
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.register() {
    onNodeWithText("Email").performTextInput("registration@test.test")
    waitForIdle()

    onNodeWithText("Heslo").performTextInput("registrationtest")
    waitForIdle()

    onNodeWithText("Registrovat").performClick()

    waitUntilDoesNotExist(
        hasTestTag("AuthProgressIndicator"),
        timeoutMillis = 3000
    )

    onNodeWithText("Přeskočit ověření").performClick()
    waitForIdle()
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.logout() {
    onNodeWithText("Nastavení").performClick()
    waitForIdle()
    onNodeWithText("Odhlásit se").performClick()
    waitUntilDoesNotExist(
        hasTestTag("AuthProgressIndicator"),
        timeoutMillis = 3000
    )
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.navigateBack() {
    onNodeWithContentDescription("Navigovat zpět").performClick()
    waitForIdle()
}