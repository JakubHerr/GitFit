package io.github.jakubherr.gitfit.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreenRoot(
    vm: AuthViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state = vm.state.collectAsStateWithLifecycle()
    DashboardScreen(state.value) { vm.onAction(AuthAction.SignOut) }
}

@Preview
@Composable
fun DashboardScreen(
    authState: AuthState,
    modifier: Modifier = Modifier,
    onAction: () -> Unit // TODO add navigation actions?
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Logged in")
        Button(onClick = onAction) {
            Text("Sign out")
        }
    }
}