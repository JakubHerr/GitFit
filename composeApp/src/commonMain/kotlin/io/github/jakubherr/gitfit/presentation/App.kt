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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jakubherr.gitfit.di.initKoin
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    initKoin {
        MaterialTheme {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "Login",
            ) {
                composable("Login") {
                    val vm = koinViewModel<AuthViewModel>()
                    LoginScreen(
                        onRegister = { email, pass -> vm.register(email, pass) },
                        onLogin = { email, pass -> vm.signIn(email, pass) },
                    )
                }
                composable("Register") { /* TODO */ }
                composable("Onboarding") { /* TODO */ }
                composable("Dashboard") { /* TODO */ }
                composable("Workout") { /* TODO */ }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onRegister: (String, String) -> Unit = { _, _ -> },
    onLogin: (String, String) -> Unit = { _, _ -> },
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
            Button(onClick = { onRegister(email, password) }) {
                Text("register")
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = { onLogin(email, password) }) {
                Text("Sign in")
            }
        }
    }
}
