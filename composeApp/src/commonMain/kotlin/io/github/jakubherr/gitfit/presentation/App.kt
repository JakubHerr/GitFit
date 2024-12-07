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
                    ExerciseListScreenRoot(
                        onCreateExerciseClick = { navController.navigate("Create Exercise") },
                        onExerciseClick = { navController.navigate("Workout") } // TODO add selected exercise to workout
                    )
                }
                composable("Exercise Detail") {
                    // TODO
                }
                composable("Create Exercise") {
                    CreateExerciseScreenRoot {
                        navController.navigate("Exercise List")
                    }
                }


                composable("Measurement") {
                    // TODO
                }
                composable("Trends") {
                    // TODO
                }

                composable("Settings") {
                    // TODO user should set some preferences during onboarding and then be able to modify them here
                }
            }
        }
    }
}
