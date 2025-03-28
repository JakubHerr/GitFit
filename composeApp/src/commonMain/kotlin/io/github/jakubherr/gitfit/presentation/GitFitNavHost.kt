package io.github.jakubherr.gitfit.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.auth.authGraph
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardAction
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.exerciseNavigation
import io.github.jakubherr.gitfit.presentation.measurement.MeasurementScreenRoot
import io.github.jakubherr.gitfit.presentation.planning.PlanningViewModel
import io.github.jakubherr.gitfit.presentation.planning.planningGraph
import io.github.jakubherr.gitfit.presentation.settings.SettingsScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutScreenRoot
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GitFitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val topLevelDestination = TopLevelDestination.entries.firstOrNull { destination?.hasRoute(it.route) == true }
    var showNavigation by remember { mutableStateOf(true) } // TODO hide navigation when user is doing a workout

    val authViewModel: AuthViewModel = koinViewModel()
    val auth by authViewModel.state.collectAsStateWithLifecycle()

    // this prevents data loss of in-memory plan
    val planViewModel: PlanningViewModel = koinViewModel()

    LaunchedEffect(auth) { println("DBG: auth state is $auth") }

    val snackbarHostState = remember { SnackbarHostState() }

    GitFitScaffold(
        showDestinations = showNavigation,
        currentDestination = topLevelDestination,
        snackbarHostState = snackbarHostState,
        onDestinationClicked = {
            navController.navigateToTopLevelDestination(it)
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = when {
                auth.user.loggedIn && auth.user.emailVerified -> DashboardRoute
                auth.user.loggedIn && !auth.user.emailVerified -> VerifyEmailRoute
                else -> LoginRoute
            }
        ) {
            authGraph(navController)

            composable<DashboardRoute> {
                DashboardScreenRoot { action ->
                    when (action) {
                        is DashboardAction.PlannedWorkoutClick, DashboardAction.UnplannedWorkoutClick, DashboardAction.ResumeWorkoutClick -> {
                            navController.navigate(WorkoutInProgressRoute)
                        }
                    }
                }
            }

            composable<WorkoutInProgressRoute> {
                WorkoutScreenRoot(
                    onAddExerciseClick = { workoutId ->
                        navController.navigate(AddExerciseToWorkoutRoute(workoutId))
                    },
                    onWorkoutFinished = { navController.popBackStack() },
                )
            }

            exerciseNavigation(navController)
            planningGraph(navController, planViewModel, snackbarHostState)

            composable<MeasurementRoute> {
                MeasurementScreenRoot()
            }

            composable<TrendsRoute> {
                // TODO
            }

            composable<SettingsRoute> {
                // TODO user should set some preferences during onboarding and then be able to modify them here
                SettingsScreenRoot()
            }
        }
    }
}

private fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
    val route: Any =
        when (destination) {
            TopLevelDestination.DASHBOARD -> DashboardRoute
            TopLevelDestination.TRENDS -> TrendsRoute
            TopLevelDestination.MEASUREMENT -> MeasurementRoute
            TopLevelDestination.PLAN -> PlanOverviewRoute
            TopLevelDestination.PROFILE -> SettingsRoute
        }
    navigate(route) {
        // popUpTo(graph.startDestinationId) TODO fix
        launchSingleTop = true
    }
}
