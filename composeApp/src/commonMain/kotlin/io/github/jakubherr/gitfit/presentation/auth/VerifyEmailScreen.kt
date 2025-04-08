package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            println("DBG: Checking for email verification...")
            if (checkEmailValidation()) break
            delay(5_000L)
        }
        onSkip()
    }

    VerifyEmailScreen(
        modifier,
        onSendVerification = { vm.onAction(AuthAction.VerifyEmail) }, // TODO rate limit in UI
        onSkip = onSkip
    )
}

@Composable
fun VerifyEmailScreen(
    modifier: Modifier = Modifier,
    onSendVerification: () -> Unit = {},
    onSkip: () -> Unit = {},
) {
    Surface {
        Column(
            modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(Res.string.email_not_verified))

            Spacer(Modifier.height(32.dp))

            Column(
                Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onSendVerification,
                    Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.send_verification_email))
                }

                Button(
                    onClick = onSkip,
                    Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.skip_verification))
                }
            }
        }
    }
}