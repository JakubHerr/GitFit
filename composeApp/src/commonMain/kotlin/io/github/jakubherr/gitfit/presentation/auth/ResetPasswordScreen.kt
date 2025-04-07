package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.enter_email
import gitfit.composeapp.generated.resources.send_reset_email
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordScreenRoot(
    vm: AuthViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var email by remember { mutableStateOf("") }

        Text(stringResource(Res.string.enter_email))

        TextField(
            email,
            onValueChange = { email = it },
            singleLine = true
        )

        // TODO rate limit in UI
        Button(onClick = {
            vm.onAction(AuthAction.RequestPasswordReset(email))
        }) {
            Text(stringResource(Res.string.send_reset_email))
        }
    }
}