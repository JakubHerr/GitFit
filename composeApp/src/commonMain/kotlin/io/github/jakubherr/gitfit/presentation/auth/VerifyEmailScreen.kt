package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.email_not_verified
import gitfit.composeapp.generated.resources.password_reset_sent
import gitfit.composeapp.generated.resources.send_verification_email
import gitfit.composeapp.generated.resources.skip_verification
import gitfit.composeapp.generated.resources.verification_email_sent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

expect suspend fun checkEmailValidation() : Boolean

@Composable
fun VerifyEmailScreenRoot(
    vm: AuthViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val finishedAction by vm.finishedAction.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(null) {
        while (true) {
            println("DBG: Checking for email verification...")
            if (checkEmailValidation()) break
            delay(5_000L)
        }
        onSkip()
    }

    // TODO maybe handle all snackbars higher in the hierarchy
    LaunchedEffect(state.error, finishedAction) {
        val error = state.error
        val action = finishedAction

        if (error != null) {
            scope.launch {
                snackbarHostState.showSnackbar(error.getMessage())
                vm.onAction(AuthAction.ErrorHandled)
            }
        }

        if (action != null && action is AuthAction.VerifyEmail) {
            scope.launch {
                snackbarHostState.showSnackbar(getString(Res.string.verification_email_sent))
                vm.onAction(AuthAction.ActionHandled)
                onSkip()
            }
        }
    }

    VerifyEmailScreen(
        modifier,
        state.loading,
        onSendVerification = { vm.onAction(AuthAction.VerifyEmail) },
        onSkip = onSkip
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
        Column(
            modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loading) CircularProgressIndicator()

            Text(stringResource(Res.string.email_not_verified))

            Spacer(Modifier.height(32.dp))

            Column(
                Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onSendVerification,
                    Modifier.fillMaxWidth(),
                    enabled = !loading
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