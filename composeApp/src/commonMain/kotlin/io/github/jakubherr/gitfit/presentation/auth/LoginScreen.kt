package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.error_email_used_already
import gitfit.composeapp.generated.resources.error_failed_to_send_email
import gitfit.composeapp.generated.resources.error_invalid_credentials
import gitfit.composeapp.generated.resources.error_no_internet
import gitfit.composeapp.generated.resources.error_password_too_weak
import gitfit.composeapp.generated.resources.error_unknown
import gitfit.composeapp.generated.resources.error_user_logged_out
import gitfit.composeapp.generated.resources.forgot_password
import gitfit.composeapp.generated.resources.register
import gitfit.composeapp.generated.resources.sign_in
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.presentation.shared.PasswordInputField
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenRoot(
    vm: AuthViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onLogin: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    val state = vm.state.collectAsStateWithLifecycle()

    LoginScreen(
        state.value,
        onAction = { action -> vm.onAction(action) },
        onForgotPassword = onForgotPassword,
    )
}

@Composable
fun LoginScreen(
    state: AuthState,
    onAction: (AuthAction) -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isValidLogin = remember(email, password) {
        email.isNotBlank() && password.isNotBlank()
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (state.loading) {
            CircularProgressIndicator()
        }
        state.error?.let { Text(it.toMessage()) }

        TextField(email, onValueChange = { email = it }, singleLine = true)
        Spacer(Modifier.height(16.dp))
        PasswordInputField(password, onPasswordChange = { password = it})

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = { onAction(AuthAction.Register(email, password)) },
                enabled = isValidLogin
            ) {
                Text(stringResource(Res.string.register))
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = { onAction(AuthAction.SignIn(email, password)) },
                enabled = isValidLogin
            ) {
                Text(stringResource(Res.string.sign_in))
            }
        }
        TextButton(onClick = onForgotPassword) {
            Text(stringResource(Res.string.forgot_password))
        }
    }
}

@Composable
fun AuthError.toMessage() = when (this) {
    AuthError.EmailInUseAlready -> stringResource(Res.string.error_email_used_already)
    AuthError.FailedToSendEmail -> stringResource(Res.string.error_failed_to_send_email)
    AuthError.Generic -> stringResource(Res.string.error_unknown)
    AuthError.InvalidCredentials -> stringResource(Res.string.error_invalid_credentials)
    AuthError.NoInternet -> stringResource(Res.string.error_no_internet)
    AuthError.PasswordTooWeak -> stringResource(Res.string.error_password_too_weak)
    AuthError.Unknown -> stringResource(Res.string.error_unknown)
    AuthError.UserLoggedOut -> stringResource(Res.string.error_user_logged_out)
}
