package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.empty_workout_history
import gitfit.composeapp.generated.resources.error_block_with_progression
import gitfit.composeapp.generated.resources.error_workout_not_found
import io.github.jakubherr.gitfit.presentation.AddExerciseToWorkoutRoute
import io.github.jakubherr.gitfit.presentation.DashboardRoute
import io.github.jakubherr.gitfit.presentation.ExerciseListRoute
import io.github.jakubherr.gitfit.presentation.HistoryRoute
import io.github.jakubherr.gitfit.presentation.LoggedInRoute
import io.github.jakubherr.gitfit.presentation.SettingsRoute
import io.github.jakubherr.gitfit.presentation.WorkoutDetailRoute
import io.github.jakubherr.gitfit.presentation.WorkoutHistoryRoute
import io.github.jakubherr.gitfit.presentation.WorkoutInProgressRoute
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardAction
import io.github.jakubherr.gitfit.presentation.dashboard.DashboardScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.exerciseNavigation
import io.github.jakubherr.gitfit.presentation.graph.HistoryScreenRoot
import io.github.jakubherr.gitfit.presentation.measurement.measurementGraph
import io.github.jakubherr.gitfit.presentation.planning.planningGraph
import io.github.jakubherr.gitfit.presentation.settings.SettingsScreenRoot
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI

fun NavGraphBuilder.loggedInGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    navigation<LoggedInRoute>(
        startDestination = DashboardRoute
    ) {
        composable<DashboardRoute> {
            val vm = navController.sharedViewModel<WorkoutViewModel>()

            DashboardScreenRoot(
                vm = vm,
            ) { action ->
                when (action) {
                    is DashboardAction.PlannedWorkoutClick, DashboardAction.UnplannedWorkoutClick, DashboardAction.ResumeWorkoutClick -> {
                        navController.navigate(WorkoutInProgressRoute)
                    }
                }
            }
        }

        composable<WorkoutInProgressRoute> {
            val vm = navController.sharedViewModel<WorkoutViewModel>()
            val scope = rememberCoroutineScope()

            WorkoutInProgressScreenRoot(
                vm = vm,
                onAction = { action ->
                    if (action !is WorkoutAction.CompleteCurrentWorkout && action !is WorkoutAction.RemoveBlock) vm.onAction(
                        action
                    )

                    when (action) {
                        is WorkoutAction.AskForExercise -> navController.navigate(AddExerciseToWorkoutRoute(action.workoutId))
                        is WorkoutAction.DeleteWorkout -> navController.popBackStack()
                        is WorkoutAction.CompleteCurrentWorkout -> {
                            val error = vm.currentWorkout.value?.error
                            if (error == null) vm.onAction(action)
                            else scope.launch { snackbarHostState.showSnackbar(error.message) }
                        }

                        is WorkoutAction.RemoveBlock -> {
                            if (action.block.progressionSettings != null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(getString(Res.string.error_block_with_progression))
                                }
                            } else vm.onAction(action)
                        }

                        else -> {}
                    }
                },
                onSaveComplete = {
                    vm.onAction(WorkoutAction.NotifyWorkoutSaved)
                    navController.popBackStack()
                }
            )
        }

        exerciseNavigation(navController)

        measurementGraph(navController, snackbarHostState)

        planningGraph(navController, snackbarHostState)

        composable<HistoryRoute> {
            HistoryScreenRoot(
                onBrowseWorkoutData = {
                    navController.navigate(WorkoutHistoryRoute)
                },
                onBrowseExerciseData = { navController.navigate(ExerciseListRoute) }
            )
        }

        composable<WorkoutHistoryRoute> {
            val vm = navController.sharedViewModel<WorkoutViewModel>()
            val completedWorkouts by vm.completedWorkouts.collectAsStateWithLifecycle()

            if (completedWorkouts.isEmpty()) {
                // TODO screen
                Text(stringResource(Res.string.empty_workout_history))
            } else {
                WorkoutListScreen(
                    completedWorkouts,
                    onWorkoutSelected = {
                        vm.onAction(WorkoutAction.SelectWorkout(it))
                        navController.navigate(WorkoutDetailRoute)
                    }
                )
            }
        }

        composable<WorkoutDetailRoute> {
            val vm = navController.sharedViewModel<WorkoutViewModel>()
            val workout = vm.selectedWorkout
            workout?.let {
                WorkoutDetailScreen(
                    it,
                    onDelete = {
                        vm.onAction(WorkoutAction.DeleteWorkout(it.id))
                        navController.popBackStack()
                    }
                )
            }
            if (workout == null) {
                Text(stringResource(Res.string.error_workout_not_found))
            }
        }

        composable<SettingsRoute> {
            SettingsScreenRoot()
        }
    }
}

// This function makes sure the fetched viewmodel is scoped to LoggedInRoute
// The same viewmodel instance is reused in loggedInGraph
// basically, when the user logs out, all viewModels that may be holding his data are destroyed
@OptIn(KoinExperimentalAPI::class)
@Composable
inline fun <reified T: ViewModel> NavHostController.sharedViewModel(): T {
    return koinNavViewModel<T>(viewModelStoreOwner = getBackStackEntry(LoggedInRoute))
}