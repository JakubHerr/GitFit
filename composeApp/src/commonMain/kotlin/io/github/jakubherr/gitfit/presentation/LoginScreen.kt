package io.github.jakubherr.gitfit.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenRoot(
    vm: AuthViewModel = koinViewModel(),
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    // val state = vm.state.collectAsStateWithLifecycle()
    LoginScreen { action -> vm.onAction(action) }
}


@Composable
fun LoginScreen(
    // state: AuthState,
    onAction: (AuthAction) -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(email, onValueChange = { email = it })

        Spacer(Modifier.height(16.dp))

        TextField(
            password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { onAction(AuthAction.Register(email, password)) }) {
                Text("register")
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = { onAction(AuthAction.SignIn(email, password)) }) {
                Text("Sign in")
            }
        }
    }
}
