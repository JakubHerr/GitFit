package io.github.jakubherr.gitfit.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import gitfit.composeapp.generated.resources.delete_account
import gitfit.composeapp.generated.resources.log_out
import io.github.jakubherr.gitfit.presentation.auth.AuthAction
import io.github.jakubherr.gitfit.presentation.auth.AuthState
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.auth.PasswordField
import io.github.jakubherr.gitfit.presentation.auth.toMessage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(modifier: Modifier = Modifier) {
    val auth: AuthViewModel = koinViewModel()

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

            Text("Please confirm account deletion by entering your password")
            PasswordField(
                password = password,
                onPasswordChange = { password = it }
            )
            Button(onClick = {
                showPasswordField = false
                onDeleteAccount(password)
            }) {
                Text("Delete account")
            }
        } else {
            Button(onLogout) { Text(stringResource(Res.string.log_out)) }

            Button(onClick = { showAccountDeletionDialog = true }) {
                Text(stringResource(Res.string.delete_account))
            }

            if (showAccountDeletionDialog) {
                ConfirmationDialog(
                    title = "Delete account",
                    text = "Are sure you want to delete your account? All of your data will be lost",
                    onDismiss = { showAccountDeletionDialog = false },
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
                state.error?.let { Text(it.toMessage()) }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    text: String,
) {
    AlertDialog(
        icon = { Icon(Icons.Default.Warning, "") },
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text("Cancel")
            }
        }
    )
}
