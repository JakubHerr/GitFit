package io.github.jakubherr.gitfit.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jakubherr.gitfit.presentation.auth.LoginScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.CreateExerciseScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutScreenRoot
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            val navController = rememberNavController()

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
                composable("Workout") {
                    WorkoutScreenRoot { navController.navigate("Exercise List") }
                }

                composable("Exercise List") {
                    ExerciseListScreenRoot { navController.navigate("Create Exercise")}
                }
                composable("Exercise Detail") {
                    // TODO
                }
                composable("Create Exercise") {
                    CreateExerciseScreenRoot()
                }


                composable("Measurement") {
                    // TODO
                }
                composable("Trends") {
                    // TODO
                }
            }
        }
    }
}
