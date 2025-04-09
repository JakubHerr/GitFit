package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.enter_email
import gitfit.composeapp.generated.resources.send_reset_email
import io.github.jakubherr.gitfit.presentation.shared.AuthCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResetPasswordScreenRoot(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val finishedAction by vm.finishedAction.collectAsStateWithLifecycle()

    LaunchedEffect(finishedAction) {
        if (finishedAction is AuthAction.RequestPasswordReset) onBack()
    }

    ResetPasswordScreen(
        modifier,
        state = state,
        onResetPassword = { vm.onAction(AuthAction.RequestPasswordReset(it)) },
    )
}

@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    state: AuthState,
    onResetPassword: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }

    Surface {
        AuthCard(modifier, state.loading) {
            OutlinedTextField(
                email,
                onValueChange = { email = it },
                singleLine = true,
                label = { Text(stringResource(Res.string.enter_email)) }
            )

            Button(
                onClick = { onResetPassword(email) },
                modifier.fillMaxWidth(),
                enabled = !state.loading,
            ) {
                Text(stringResource(Res.string.send_reset_email))
            }
        }
    }
}
