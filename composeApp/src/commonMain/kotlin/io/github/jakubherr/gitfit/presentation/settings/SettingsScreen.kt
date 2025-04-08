package io.github.jakubherr.gitfit.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.confirm_account_deletion
import gitfit.composeapp.generated.resources.delete
import gitfit.composeapp.generated.resources.delete_account
import gitfit.composeapp.generated.resources.delete_account_explanaiton
import gitfit.composeapp.generated.resources.log_out
import io.github.jakubherr.gitfit.presentation.auth.AuthAction
import io.github.jakubherr.gitfit.presentation.auth.AuthState
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.shared.PasswordInputField
import io.github.jakubherr.gitfit.presentation.auth.toMessage
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreenRoot(
    auth: AuthViewModel,
    modifier: Modifier = Modifier,
) {

    SettingsScreen(
        onLogout = { auth.onAction(AuthAction.SignOut) },
        onDeleteAccount = { auth.onAction(AuthAction.DeleteAccount(it))},
        authState = auth.state.collectAsState()
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onDeleteAccount: (String) -> Unit,
    authState: State<AuthState>? = null,
    ) {
    var showAccountDeletionDialog by remember { mutableStateOf(false) }
    var showPasswordField by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize()) {
        if (showPasswordField) {
            var password by remember { mutableStateOf("") }

            Text(stringResource(Res.string.confirm_account_deletion))
            PasswordInputField(
                password = password,
                onPasswordChange = { password = it }
            )
            Button(onClick = {
                showPasswordField = false
                onDeleteAccount(password)
            }) {
                Text(stringResource(Res.string.delete_account))
            }
        } else {
            Button(onLogout) { Text(stringResource(Res.string.log_out)) }

            Button(onClick = { showAccountDeletionDialog = true }) {
                Text(stringResource(Res.string.delete_account))
            }

            if (showAccountDeletionDialog) {
                ConfirmationDialog(
                    title = stringResource(Res.string.delete_account),
                    text = stringResource(Res.string.delete_account_explanaiton),
                    dismissText = stringResource(Res.string.cancel),
                    onDismiss = { showAccountDeletionDialog = false },
                    confirmText = stringResource(Res.string.delete),
                    onConfirm = {
                        showAccountDeletionDialog = false
                        showPasswordField = true
                    },
                )
            }

            authState?.value?.let { state ->
                Spacer(Modifier.width(32.dp))
                Text("DEBUG INFO:\n user id: ${state.user.id} \n email verified: ${state.user.emailVerified}")

                // TODO some better UI
                if (state.loading) CircularProgressIndicator()
            }
        }
    }
}
