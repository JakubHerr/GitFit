package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
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
    planViewModel: PlanningViewModel,
    workoutViewModel: WorkoutViewModel,
    snackbarHostState: SnackbarHostState,
) {
    fun handleError(error: Plan.Error?, scope: CoroutineScope) {
        if (error == null) navController.popBackStack()
        else scope.launch {
            snackbarHostState.showSnackbar(error.message)
            planViewModel.onAction(PlanAction.ErrorHandled)
        }
    }

    composable<PlanOverviewRoute> {
        PlanListScreenRoot(
            vm = planViewModel,
            onCreateNewPlan = { navController.navigate(PlanCreationRoute) },
            onPlanSelected = { navController.navigate(PlanDetailRoute(it.id)) }
        )
    }

    composable<PlanDetailRoute> { backstackEntry ->
        val planId = backstackEntry.toRoute<PlanDetailRoute>().planId
        val userPlans = planViewModel.userPlans.collectAsStateWithLifecycle(emptyList())
        val scope = rememberCoroutineScope()
        val currentWorkout by workoutViewModel.currentWorkout.collectAsStateWithLifecycle()

        // TODO differentiate user and predefined plan
        val userPlan = userPlans.value.find { it.id == planId }

        userPlan?.let { plan ->
            PlanDetailScreen(
                plan,
                onWorkoutSelected = { workout ->
                    println("DBG: Plan selected: ${plan.id}, workout index ${workout.idx}")

                    if (currentWorkout == null) {
                        workoutViewModel.onAction(WorkoutAction.StartPlannedWorkout(plan, workout.idx))
                        navController.navigate(WorkoutInProgressRoute)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("A different workout is already in progress!")
                        }
                    }

                },
                onAction = { action ->
                    planViewModel.onAction(action)
                    if (action is PlanAction.DeletePlan) navController.popBackStack()
                    if (action is PlanAction.EditPlan) navController.navigate(PlanCreationRoute)
                }
            )
        }
    }

    composable<AddExerciseToPlanRoute> { backstackEntry ->
        val idx = backstackEntry.toRoute<AddExerciseToPlanRoute>().workoutIdx

        ExerciseListScreenRoot(
            vm = koinViewModel(),
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = { exercise ->
                planViewModel.onAction(PlanAction.AddExercise(idx, exercise))
                navController.popBackStack()
            },
        )
    }

    composable<PlanCreationRoute> {
        val scope = rememberCoroutineScope()

        PlanCreationScreen(
            planViewModel.plan,
            onAction = { action ->
                planViewModel.onAction(action)
                when (action) {
                    PlanAction.DiscardPlan -> navController.popBackStack()
                    PlanAction.SavePlan -> handleError(planViewModel.error, scope)
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

        PlanWorkoutCreationScreen(
            workoutPlan = planViewModel.plan.workoutPlans[idx],
            onAction = { planViewModel.onAction(it) },
            onAddExerciseClick = { workoutIdx ->
                navController.navigate(AddExerciseToPlanRoute(workoutIdx))
            },
            onSave = { handleError(planViewModel.error, scope) },
            onEditProgression = { block -> navController.navigate(EditProgressionRoute(idx, block.idx)) }
        )
    }

    composable<EditProgressionRoute> { backstackEntry ->
        val entry = backstackEntry.toRoute<EditProgressionRoute>()
        val workout = planViewModel.plan.workoutPlans.getOrNull(entry.workoutIdx)
        val block = workout?.blocks?.getOrNull(entry.blockIdx)

        if (block == null) {
            Text("Error: block not found")
        } else {
            EditProgressionScreenRoot(
                block,
                onCancel = { navController.popBackStack() },
                onDelete = {
                    planViewModel.onAction(PlanAction.DeleteProgression(workout, block))
                    navController.popBackStack()
                },
                onSave = {
                    planViewModel.onAction(PlanAction.SaveProgression(workout, block, it))
                    navController.popBackStack()
                }
            )
        }
    }
}