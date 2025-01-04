package io.github.jakubherr.gitfit.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.log_out
import io.github.jakubherr.gitfit.presentation.auth.AuthAction
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    modifier: Modifier = Modifier
) {
    val auth: AuthViewModel = koinViewModel()

    SettingsScreen() {
        auth.onAction(AuthAction.SignOut)
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
) {
    Column(modifier.fillMaxSize()) {
        Button(onLogout) {
            Text(stringResource(Res.string.log_out))
        }
    }
}