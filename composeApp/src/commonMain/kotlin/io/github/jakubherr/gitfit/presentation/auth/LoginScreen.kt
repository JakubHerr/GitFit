package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.email
import gitfit.composeapp.generated.resources.error_email_used_already
import gitfit.composeapp.generated.resources.error_failed_to_send_email
import gitfit.composeapp.generated.resources.error_invalid_credentials
import gitfit.composeapp.generated.resources.error_invalid_user
import gitfit.composeapp.generated.resources.error_no_internet
import gitfit.composeapp.generated.resources.error_password_too_weak
import gitfit.composeapp.generated.resources.error_unknown
import gitfit.composeapp.generated.resources.error_user_logged_out
import gitfit.composeapp.generated.resources.forgot_password
import gitfit.composeapp.generated.resources.ic_launcher_foreground
import gitfit.composeapp.generated.resources.register
import gitfit.composeapp.generated.resources.sign_in
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.presentation.shared.PasswordInputField
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun LoginScreenRoot(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
    onForgotPassword: () -> Unit = {},
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LoginScreen(
        state,
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

    // val locale = koinInject<PlatformLocalization>()

    Surface {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("GitFit", style = MaterialTheme.typography.headlineLarge)
            Image(
                imageVector = vectorResource(Res.drawable.ic_launcher_foreground),
                "",
                modifier = Modifier.size(128.dp),
                contentScale = ContentScale.FillBounds,
            )

            if (state.loading) {
                CircularProgressIndicator()
            }

            Card(
                modifier = Modifier.sizeIn(maxWidth = 512.dp).padding(16.dp),
            ) {
                Column(
                    Modifier.padding(16.dp),
                ) {
                    OutlinedTextField(
                        email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(stringResource(Res.string.email)) },
                    )
                    Spacer(Modifier.height(16.dp))
                    PasswordInputField(
                        password,
                        onPasswordChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Button(
                            onClick = { onAction(AuthAction.Register(email, password)) },
                            enabled = isValidLogin,
                        ) {
                            Text(stringResource(Res.string.register))
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = { onAction(AuthAction.SignIn(email, password)) },
                            enabled = isValidLogin,
                        ) {
                            Text(stringResource(Res.string.sign_in))
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = onForgotPassword) {
                            Text(stringResource(Res.string.forgot_password))
                        }
                    }
                }
            }
        }
    }
}

suspend fun AuthError.getMessage() =
    when (this) {
        AuthError.EmailInUseAlready -> getString(Res.string.error_email_used_already)
        AuthError.FailedToSendEmail -> getString(Res.string.error_failed_to_send_email)
        AuthError.Generic -> getString(Res.string.error_unknown)
        AuthError.InvalidCredentials -> getString(Res.string.error_invalid_credentials)
        AuthError.NoInternet -> getString(Res.string.error_no_internet)
        AuthError.PasswordTooWeak -> getString(Res.string.error_password_too_weak)
        AuthError.Unknown -> getString(Res.string.error_unknown)
        AuthError.UserLoggedOut -> getString(Res.string.error_user_logged_out)
        AuthError.InvalidUser -> getString(Res.string.error_invalid_user)
    }
