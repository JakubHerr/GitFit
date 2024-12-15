package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.jakubherr.gitfit.presentation.auth.LoginScreenRoot
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardAction
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.CreateExerciseScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GitFitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val workoutViewModel: WorkoutViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = LoginRoute, // DashboardRoute,
    ) {
        composable<LoginRoute> {
            LoginScreenRoot { }
        }

        composable<OnboardingRoute> { /* TODO */ }

        composable<DashboardRoute> {
            DashboardScreenRoot() { action ->
                when(action) {
                    is DashboardAction.PlannedWorkoutClick -> { /* TODO */ }
                    is DashboardAction.UnplannedWorkoutClick -> {
                        workoutViewModel.startWorkout()
                        navController.navigate(WorkoutRoute)
                    }
                }
            }
        }

        // unplanned workout
        composable<WorkoutRoute> {
            WorkoutScreenRoot(workoutViewModel) {
                navController.navigate(AddExerciseToWorkoutRoute(workoutViewModel.currentWorkout.value!!.id)) // TODO this is not ideal
            }
        }

        // TODO implement adding exercise by sending workout id?
        composable<ExerciseListRoute> {
            ExerciseListScreenRoot(
                onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
                onExerciseClick = { navController.navigate(WorkoutRoute) } // TODO add selected exercise to workout
            )
        }
        composable<AddExerciseToWorkoutRoute> {
            ExerciseListScreenRoot(
                onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
                onExerciseClick = { exercise ->
                    workoutViewModel.onAction(WorkoutAction.AddBlock(workoutViewModel.currentWorkout.value!!.id, exercise.id))
                    navController.popBackStack()
                }
            )
        }

        composable<ExerciseDetailRoute> {
            // TODO
        }
        composable<CreateExerciseRoute> {
            CreateExerciseScreenRoot {
                navController.popBackStack()
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