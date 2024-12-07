package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.jakubherr.gitfit.presentation.exercise.CreateExerciseScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutScreenRoot

@Composable
fun GitFitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute,
    ) {
        composable<OnboardingRoute> { /* TODO */ }

        composable<DashboardRoute> {
            DashboardScreenRoot()
        }

        composable<WorkoutRoute> {
            WorkoutScreenRoot { navController.navigate(ExerciseListRoute) }
        }

        composable<ExerciseListRoute> {
            ExerciseListScreenRoot(
                onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
                onExerciseClick = { navController.navigate(WorkoutRoute) } // TODO add selected exercise to workout
            )
        }
        composable<ExerciseDetailRoute> {
            // TODO
        }
        composable<CreateExerciseRoute> {
            CreateExerciseScreenRoot {
                navController.navigate(ExerciseListRoute)
            }
        }

        composable<MeasurementRoute> {
            // TODO
        }

        composable<TrendsRoute> {
            // TODO
        }

        composable<SettingsRoute> {
            // TODO user should set some preferences during onboarding and then be able to modify them here
        }
    }
}