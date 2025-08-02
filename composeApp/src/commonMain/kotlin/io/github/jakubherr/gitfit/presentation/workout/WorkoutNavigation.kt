package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.empty_workout_history
import gitfit.composeapp.generated.resources.error_block_with_progression
import gitfit.composeapp.generated.resources.error_workout_not_found
import io.github.jakubherr.gitfit.presentation.AddExerciseToWorkoutRoute
import io.github.jakubherr.gitfit.presentation.WorkoutDetailRoute
import io.github.jakubherr.gitfit.presentation.WorkoutHistoryRoute
import io.github.jakubherr.gitfit.presentation.WorkoutInProgressRoute
import io.github.jakubherr.gitfit.presentation.shared.toMessage
import io.github.jakubherr.gitfit.presentation.sharedViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

fun NavGraphBuilder.workoutNavigation(
    navController: NavHostController,
    showSnackbar: (String) -> Unit,
) {
    composable<WorkoutInProgressRoute> {
        val vm = navController.sharedViewModel<WorkoutViewModel>()
        val scope = rememberCoroutineScope()

        WorkoutInProgressScreenRoot(
            vm = vm,
            onAction = { action ->
                if (action !is WorkoutAction.CompleteCurrentWorkout && action !is WorkoutAction.RemoveBlock) {
                    vm.onAction(
                        action,
                    )
                }

                when (action) {
                    is WorkoutAction.AskForExercise -> navController.navigate(AddExerciseToWorkoutRoute)
                    is WorkoutAction.DeleteWorkout -> navController.popBackStack()
                    is WorkoutAction.CompleteCurrentWorkout -> {
                        val error = vm.currentWorkout.value?.error
                        if (error == null) {
                            vm.onAction(action)
                        } else {
                            scope.launch {
                                showSnackbar(error.toMessage())
                            }
                        }
                    }
                    is WorkoutAction.RemoveBlock -> {
                        if (action.block.progressionSettings != null) {
                            scope.launch { showSnackbar(getString(Res.string.error_block_with_progression)) }
                        } else {
                            vm.onAction(action)
                        }
                    }
                    else -> {}
                }
            },
            onSaveComplete = {
                vm.onAction(WorkoutAction.NotifyWorkoutSaved)
                navController.popBackStack()
            },
        )
    }

    composable<WorkoutHistoryRoute> {
        val vm = navController.sharedViewModel<WorkoutViewModel>()
        val completedWorkouts by vm.completedWorkouts.collectAsStateWithLifecycle()

        if (completedWorkouts.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(Res.string.empty_workout_history))
            }
        } else {
            WorkoutListScreen(
                completedWorkouts.sortedByDescending { it.date },
                onWorkoutSelected = {
                    vm.onAction(WorkoutAction.SelectWorkout(it))
                    navController.navigate(WorkoutDetailRoute)
                },
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
                },
            )
        }
        if (workout == null) {
            Text(stringResource(Res.string.error_workout_not_found))
        }
    }
}
