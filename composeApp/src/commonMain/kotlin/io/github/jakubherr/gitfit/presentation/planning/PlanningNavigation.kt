package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.presentation.AddExerciseToPlanRoute
import io.github.jakubherr.gitfit.presentation.CreateExerciseRoute
import io.github.jakubherr.gitfit.presentation.EditProgressionRoute
import io.github.jakubherr.gitfit.presentation.PlanCreationRoute
import io.github.jakubherr.gitfit.presentation.PlanDetailRoute
import io.github.jakubherr.gitfit.presentation.PlanOverviewRoute
import io.github.jakubherr.gitfit.presentation.PlanningWorkoutRoute
import io.github.jakubherr.gitfit.presentation.WorkoutInProgressRoute
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.planningGraph(
    navController: NavHostController,
    viewModel: PlanningViewModel,
    snackbarHostState: SnackbarHostState,
) {
    fun handleError(error: Plan.Error?, scope: CoroutineScope) {
        if (error == null) navController.popBackStack()
        else scope.launch {
            snackbarHostState.showSnackbar(error.message)
            viewModel.onAction(PlanAction.ErrorHandled)
        }
    }

    composable<PlanOverviewRoute> {
        PlanOverviewScreenRoot(
            vm = viewModel,
            onCreateNewPlan = { navController.navigate(PlanCreationRoute) },
            onPlanSelected = { navController.navigate(PlanDetailRoute(it.id)) }
        )
    }

    composable<PlanDetailRoute> { backstackEntry ->
        val planId = backstackEntry.toRoute<PlanDetailRoute>().planId
        val userPlans = viewModel.userPlans.collectAsStateWithLifecycle(emptyList())

        // TODO differentiate user and predefined plan
        val userPlan = userPlans.value.find { it.id == planId }

        val workoutViewModel: WorkoutViewModel = koinViewModel()

        userPlan?.let { plan ->
            PlanDetailScreen(
                plan,
                onWorkoutSelected = { workout ->
                    // TODO should handle edge case where user already has a workout in progress
                    println("DBG: Plan selected: ${plan.id}, workout index ${workout.idx}")
                    workoutViewModel.onAction(WorkoutAction.StartPlannedWorkout(plan.id, workout.idx))
                    navController.navigate(WorkoutInProgressRoute)

                },
                onAction = { action ->
                    viewModel.onAction(action)
                    if (action is PlanAction.DeletePlan) navController.popBackStack()
                    if (action is PlanAction.EditPlan) navController.navigate(PlanCreationRoute)
                }
            )
        }
    }

    composable<AddExerciseToPlanRoute> { backstackEntry ->
        val idx = backstackEntry.toRoute<AddExerciseToPlanRoute>().workoutIdx

        ExerciseListScreenRoot(
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = { exercise ->
                viewModel.onAction(PlanAction.AddExercise(idx, exercise))
                navController.popBackStack()
            },
        )
    }

    composable<PlanCreationRoute> {
        val scope = rememberCoroutineScope()

        PlanCreationScreen(
            viewModel.plan,
            onAction = { action ->
                viewModel.onAction(action)
                when (action) {
                    PlanAction.DiscardPlan -> navController.popBackStack()
                    PlanAction.SavePlan -> handleError(viewModel.error, scope)
                    else -> {}
                }
            },
            onWorkoutSelected = { workoutIdx ->
                navController.navigate(PlanningWorkoutRoute(workoutIdx))
            },
        )
    }

    composable<PlanningWorkoutRoute> { backstackEntry ->
        val idx = backstackEntry.toRoute<PlanningWorkoutRoute>().workoutIdx
        val scope = rememberCoroutineScope()

        PlanWorkoutDetailScreen(
            workout = viewModel.plan.workouts[idx],
            onAction = { viewModel.onAction(it) },
            onAddExerciseClick = { workoutIdx ->
                navController.navigate(AddExerciseToPlanRoute(workoutIdx))
            },
            onSave = { handleError(viewModel.error, scope) },
            onEditProgression = { block -> navController.navigate(EditProgressionRoute(idx, block.idx)) }
        )
    }

    composable<EditProgressionRoute> { backstackEntry ->
        val entry = backstackEntry.toRoute<EditProgressionRoute>()
        val workout = viewModel.plan.workouts.getOrNull(entry.workoutIdx)
        val block = workout?.blocks?.getOrNull(entry.blockIdx)

        if (block == null) {
            Text("Error: block not found")
        } else {
            EditProgressionScreenRoot(
                block,
                onCancel = { navController.popBackStack() },
                onDelete = {
                    viewModel.onAction(PlanAction.DeleteProgression(workout, block))
                    navController.popBackStack()
                },
                onSave = {
                    viewModel.onAction(PlanAction.SaveProgression(workout, block, it))
                    navController.popBackStack()
                }
            )
        }
    }
}