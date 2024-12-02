package io.github.jakubherr.gitfit.presentation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            val navController = rememberNavController()
            val authViewModel = koinViewModel<AuthViewModel>()
            val state = authViewModel.state.collectAsStateWithLifecycle()

//            LaunchedEffect(state.value) {
//                println("NAVIGATION TRIGGERED")
//                navController.navigate(if (state.value.loggedIn) "Dashboard" else "Login")
//            }

            NavHost(
                navController = navController,
                startDestination = "Workout",
            ) {
                composable("Login") {
                    LoginScreenRoot(
                        onLogin = {
                            println("Navigating to dahboard")
                            navController.navigate("Dashboard") {
                                popUpTo("Login") { inclusive = true }
                            }
                        },
                    )
                }
                composable("Register") { /* TODO */ }
                composable("Onboarding") { /* TODO */ }
                composable("Dashboard") {
                    DashboardScreenRoot()
                }
                composable("Workout") { WorkoutScreenRoot() }
            }
        }
    }
}
