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

class AuthTest {

    // This test validates that user can register a new account and then delete it from settings
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun authTest() = runComposeUiTest {
        setContent {
            App()
        }

        register()
        onNodeWithText("Nastavení").performClick()
        onNodeWithTag("DeleteAccountButton1").performClick()
        onNodeWithTag("ConfirmDialogButton").performClick()
        onNodeWithText("Heslo").performTextInput("registrationtest")
        onNodeWithTag("DeleteAccountButton2").performClick()
        waitUntilDoesNotExist(
            hasTestTag("AuthProgressIndicator"),
            timeoutMillis = 10000
        )
    }
}