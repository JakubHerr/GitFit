package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.email_not_verified
import gitfit.composeapp.generated.resources.send_verification_email
import gitfit.composeapp.generated.resources.skip_verification
import io.github.jakubherr.gitfit.presentation.shared.AuthCard
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

expect suspend fun checkEmailValidation(): Boolean

@Composable
fun VerifyEmailScreenRoot(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val finishedAction by vm.finishedAction.collectAsStateWithLifecycle()

    LaunchedEffect(null) {
        while (true) {
            if (checkEmailValidation()) break
            delay(5_000L)
        }
        onSkip()
    }

    LaunchedEffect(finishedAction) {
        if (finishedAction is AuthAction.VerifyEmail) onSkip()
    }

    VerifyEmailScreen(
        modifier,
        state.loading,
        onSendVerification = { vm.onAction(AuthAction.VerifyEmail) },
        onSkip = onSkip,
    )
}

@Composable
fun VerifyEmailScreen(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    onSendVerification: () -> Unit = {},
    onSkip: () -> Unit = {},
) {
    Surface {
        AuthCard(
            modifier,
            loading,
        ) {
            Text(stringResource(Res.string.email_not_verified))

            Button(
                onClick = onSendVerification,
                Modifier.fillMaxWidth(),
                enabled = !loading,
            ) {
                Text(stringResource(Res.string.send_verification_email))
            }

            Button(
                onClick = onSkip,
                Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(Res.string.skip_verification))
            }
        }
    }
}
