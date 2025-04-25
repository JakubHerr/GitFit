package io.github.jakubherr.gitfit.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.about_app
import gitfit.composeapp.generated.resources.app_explanation
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.confirm_account_deletion
import gitfit.composeapp.generated.resources.delete
import gitfit.composeapp.generated.resources.delete_account
import gitfit.composeapp.generated.resources.delete_account_explanaiton
import gitfit.composeapp.generated.resources.log_out
import gitfit.composeapp.generated.resources.privacy_policy
import io.github.jakubherr.gitfit.presentation.auth.AuthAction
import io.github.jakubherr.gitfit.presentation.auth.AuthState
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.shared.AuthCard
import io.github.jakubherr.gitfit.presentation.shared.ConfirmationDialog
import io.github.jakubherr.gitfit.presentation.shared.PasswordInputField
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreenRoot(
    auth: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val state by auth.state.collectAsStateWithLifecycle()

    SettingsScreen(
        onLogout = { auth.onAction(AuthAction.SignOut) },
        onDeleteAccount = { auth.onAction(AuthAction.DeleteAccount(it)) },
        authState = state,
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onDeleteAccount: (String) -> Unit,
    authState: AuthState,
) {
    var showAccountDeletionDialog by remember { mutableStateOf(false) }
    var showPasswordInput by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) InfoDialog(onDismiss = { showInfoDialog = false })

    if (showAccountDeletionDialog) {
        ConfirmationDialog(
            title = stringResource(Res.string.delete_account),
            text = stringResource(Res.string.delete_account_explanaiton),
            dismissText = stringResource(Res.string.cancel),
            onDismiss = { showAccountDeletionDialog = false },
            confirmText = stringResource(Res.string.delete),
            onConfirm = {
                showAccountDeletionDialog = false
                showPasswordInput = true
            },
        )
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        IconButton(
            onClick = { showInfoDialog = true },
        ) {
            Icon(
                Icons.Default.Info,
                stringResource(Res.string.about_app),
                modifier = Modifier.size(80.dp)
            )
        }
        if (showPasswordInput) {
            var password by remember { mutableStateOf("") }

            AuthCard(
                modifier,
                authState.loading
            ) {
                Text(stringResource(Res.string.confirm_account_deletion))
                PasswordInputField(
                    password = password,
                    onPasswordChange = { password = it },
                )

                Button(
                    onClick = {
                        showPasswordInput = false
                        onDeleteAccount(password)
                    },
                    Modifier.fillMaxWidth().testTag("DeleteAccountButton2"),
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text(stringResource(Res.string.delete_account))
                }
            }
        } else {
            AuthCard(
                Modifier,
                loading = authState.loading
            ) {
                Button(onLogout) { Text(stringResource(Res.string.log_out)) }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { showAccountDeletionDialog = true },
                    modifier = Modifier.testTag("DeleteAccountButton1"),
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text(stringResource(Res.string.delete_account))
                }
            }
        }
    }
}

@Composable
fun InfoDialog(
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
        ) {
            SelectionContainer {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.app_explanation),
                )
            }

            SelectionContainer {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.privacy_policy),
                )
            }
        }
    }
}
