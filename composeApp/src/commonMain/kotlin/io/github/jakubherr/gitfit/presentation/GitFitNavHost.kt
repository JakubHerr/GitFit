package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val topLevelDestination = TopLevelDestination.entries.firstOrNull { destination?.hasRoute(it.route) == true }
    var showNavigation by remember { mutableStateOf(true) } // TODO hide navigation when user is doing a workout

    val workoutViewModel: WorkoutViewModel = koinViewModel()

    GitFitScaffold(
        showDestinations = showNavigation,
        currentDestination = topLevelDestination,
        onDestinationClicked = {
            navController.navigateToTopLevelDestination(it)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
        ) {
            composable<LoginRoute> {
                LoginScreenRoot { }
            }

            composable<OnboardingRoute> { /* TODO */ }

            composable<DashboardRoute> {
                DashboardScreenRoot() { action ->
                    when(action) {
                        is DashboardAction.PlannedWorkoutClick -> { /* TODO */ }
                        is DashboardAction.UnplannedWorkoutClick -> navController.navigate(WorkoutRoute)
                    }
                }
            }

            // unplanned workout
            composable<WorkoutRoute> {
                WorkoutScreenRoot(
                    workoutViewModel,
                    onAddExerciseClick = { navController.navigate(AddExerciseToWorkoutRoute(workoutViewModel.currentWorkout.value!!.id)) }, // TODO this is not ideal
                    onWorkoutFinished = { navController.popBackStack() }
                )
            }

            composable<ExerciseListRoute> {
                ExerciseListScreenRoot(
                    onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
                    onExerciseClick = { navController.navigate(WorkoutRoute) }
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

            composable<PlanningRoute> {

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
}

private fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
    val route: Any = when(destination) {
        TopLevelDestination.DASHBOARD -> DashboardRoute
        TopLevelDestination.TRENDS -> TrendsRoute
        TopLevelDestination.MEASUREMENT -> MeasurementRoute
        TopLevelDestination.PLAN -> PlanningRoute
        TopLevelDestination.PROFILE -> SettingsRoute
    }
    navigate(route) {
        // popUpTo(graph.startDestinationId) TODO fix
        launchSingleTop = true
    }
}
