package io.github.jakubherr.gitfit.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseViewModel
import io.github.jakubherr.gitfit.presentation.exercise.exerciseNavigation
import io.github.jakubherr.gitfit.presentation.graph.HistoryScreenRoot
import io.github.jakubherr.gitfit.presentation.measurement.measurementGraph
import io.github.jakubherr.gitfit.presentation.planning.PlanAction
import io.github.jakubherr.gitfit.presentation.planning.PlanningViewModel
import io.github.jakubherr.gitfit.presentation.planning.planningGraph
import io.github.jakubherr.gitfit.presentation.settings.SettingsScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutDetailScreen
import io.github.jakubherr.gitfit.presentation.workout.WorkoutListScreen
import io.github.jakubherr.gitfit.presentation.workout.WorkoutInProgressScreenRoot
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

    /*
        these ViewModels are shared globally so that state like current workout or selected exercise can be passed between
        nav destinations without fetching them from the DB that is extremely slow in offline mode
    */
    val planViewModel: PlanningViewModel = koinViewModel()
    val exerciseViewModel: ExerciseViewModel = koinViewModel()
    val workoutViewModel: WorkoutViewModel = koinViewModel()

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

                WorkoutInProgressScreenRoot(
                    vm = workoutViewModel,
                    onAction = { action ->
                        if (action !is WorkoutAction.CompleteCurrentWorkout && action !is WorkoutAction.RemoveBlock) workoutViewModel.onAction(
                            action
                        )

                        when (action) {
                            is WorkoutAction.AskForExercise -> navController.navigate(AddExerciseToWorkoutRoute(action.workoutId))
                            is WorkoutAction.DeleteWorkout -> navController.popBackStack()
                            is WorkoutAction.CompleteCurrentWorkout -> {
                                val error = workoutViewModel.currentWorkout.value?.error
                                if (error == null) workoutViewModel.onAction(action)
                                else scope.launch { snackbarHostState.showSnackbar(error.message) }
                            }

                            is WorkoutAction.RemoveBlock -> {
                                if (action.block.progressionSettings != null) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Blocks with progression can not be removed")
                                    }
                                } else workoutViewModel.onAction(action)
                            }

                            else -> {}
                        }
                    },
                    onSaveComplete = {
                        workoutViewModel.onAction(WorkoutAction.NotifyWorkoutSaved)
                        navController.popBackStack()
                    }
                )
            }

            exerciseNavigation(navController, exerciseViewModel, workoutViewModel)

            measurementGraph(navController, snackbarHostState)

            planningGraph(navController, planViewModel, snackbarHostState)

            composable<HistoryRoute> {
                HistoryScreenRoot(
                    onBrowseWorkoutData = {
                        navController.navigate(WorkoutHistoryRoute)
                    },
                    onBrowseExerciseData = { navController.navigate(ExerciseListRoute) }
                )
            }

            composable<WorkoutHistoryRoute> {
                val completedWorkouts by workoutViewModel.completedWorkouts.collectAsStateWithLifecycle()

                if (completedWorkouts.isEmpty()) {
                    // TODO screen
                    Text("No workout history")
                } else {
                    WorkoutListScreen(
                        completedWorkouts,
                        onWorkoutSelected = {
                            workoutViewModel.onAction(WorkoutAction.SelectWorkout(it))
                            navController.navigate(WorkoutDetailRoute)
                        }
                    )
                }
            }

            composable<WorkoutDetailRoute> {
                val workout = workoutViewModel.selectedWorkout
                workout?.let {
                    WorkoutDetailScreen(
                        it,
                        onDelete = {
                            workoutViewModel.onAction(WorkoutAction.DeleteWorkout(it.id))
                            navController.popBackStack()
                        }
                    )
                }
                if (workout == null) {
                    Text("Something went wrong")
                }
            }

            composable<SettingsRoute> {
                SettingsScreenRoot()
            }
        }
    }
}

private fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
    val route: Any =
        when (destination) {
            TopLevelDestination.DASHBOARD -> DashboardRoute
            TopLevelDestination.HISTORY -> HistoryRoute
            TopLevelDestination.MEASUREMENT -> MeasurementRoute
            TopLevelDestination.PLAN -> PlanOverviewRoute
            TopLevelDestination.PROFILE -> SettingsRoute
        }
    navigate(route) {
        // popUpTo(graph.startDestinationId) TODO fix
        launchSingleTop = true
    }
}
