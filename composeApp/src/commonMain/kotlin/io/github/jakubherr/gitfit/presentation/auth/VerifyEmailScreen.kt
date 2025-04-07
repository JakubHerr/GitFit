package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.email_not_verified
import gitfit.composeapp.generated.resources.send_verification_email
import gitfit.composeapp.generated.resources.skip_verification
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

expect suspend fun checkEmailValidation() : Boolean

@Composable
fun VerifyEmailScreenRoot(
    vm: AuthViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
) {
    LaunchedEffect(null) {
        while (true) {
            delay(5_000L)
            println("DBG: Checking for email verification...")
            if (checkEmailValidation()) break
        }
        onSkip()
    }

    Column(modifier.fillMaxSize()) {
        Text(stringResource(Res.string.email_not_verified))

        // TODO rate limit in UI
        Button({ vm.onAction(AuthAction.VerifyEmail)}) {
            Text(stringResource(Res.string.send_verification_email))
        }

        Button(onSkip) {
            Text(stringResource(Res.string.skip_verification))
        }
    }
}