package io.github.jakubherr.gitfit.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.github.jakubherr.gitfit.presentation.graph.GraphScreenRoot
import io.github.jakubherr.gitfit.presentation.measurement.measurementGraph
import io.github.jakubherr.gitfit.presentation.planning.PlanAction
import io.github.jakubherr.gitfit.presentation.planning.PlanningViewModel
import io.github.jakubherr.gitfit.presentation.planning.planningGraph
import io.github.jakubherr.gitfit.presentation.settings.SettingsScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GitFitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val topLevelDestination = TopLevelDestination.entries.firstOrNull { destination?.hasRoute(it.route) == true }
    var showNavigation by remember { mutableStateOf(true) }

    val authViewModel: AuthViewModel = koinViewModel()
    val auth by authViewModel.state.collectAsStateWithLifecycle()

    // this prevents data loss of in-memory plan
    val planViewModel: PlanningViewModel = koinViewModel()

    LaunchedEffect(auth) {
        println("DBG: auth state is ${auth.user.loggedIn}")
        planViewModel.onAction(PlanAction.DiscardPlan)
    }

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
                val scope = rememberCoroutineScope()
                val workoutVm: WorkoutViewModel = koinViewModel()

                WorkoutScreenRoot(
                    onAction = { action ->
                        if (action !is WorkoutAction.CompleteCurrentWorkout) workoutVm.onAction(action)

                        when (action) {
                            is WorkoutAction.AskForExercise -> navController.navigate(AddExerciseToWorkoutRoute(action.workoutId))
                            is WorkoutAction.DeleteWorkout -> navController.popBackStack()
                            is WorkoutAction.CompleteCurrentWorkout -> {
                                val error = workoutVm.currentWorkout.value?.error
                                if (error == null) {
                                    workoutVm.onAction(action)
                                    // navController.popBackStack()
                                }
                                else scope.launch { snackbarHostState.showSnackbar(error.message) }
                            }
                            else -> { }
                        }
                    },
                    onSaveComplete = { navController.popBackStack() }

                )
            }

            exerciseNavigation(navController)

            measurementGraph(navController, snackbarHostState)

            planningGraph(navController, planViewModel, snackbarHostState)



            composable<TrendsRoute> {
                GraphScreenRoot() {
                    navController.navigate(ExerciseListRoute)
                }
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
