package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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

    Column(Modifier.fillMaxSize()) {
        AuthCard(modifier, state.loading) {
            OutlinedTextField(
                email,
                onValueChange = { email = it },
                singleLine = true,
                label = { Text(stringResource(Res.string.enter_email)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
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
