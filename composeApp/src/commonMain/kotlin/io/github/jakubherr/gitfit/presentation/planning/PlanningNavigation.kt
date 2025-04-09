package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.error_block_not_found
import gitfit.composeapp.generated.resources.error_workout_in_progress
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.presentation.AddExerciseToPlanRoute
import io.github.jakubherr.gitfit.presentation.CreateExerciseRoute
import io.github.jakubherr.gitfit.presentation.EditProgressionRoute
import io.github.jakubherr.gitfit.presentation.PlanCreationRoute
import io.github.jakubherr.gitfit.presentation.PlanDetailRoute
import io.github.jakubherr.gitfit.presentation.PlanOverviewRoute
import io.github.jakubherr.gitfit.presentation.PlanningWorkoutRoute
import io.github.jakubherr.gitfit.presentation.TopLevelDestination
import io.github.jakubherr.gitfit.presentation.WorkoutInProgressRoute
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseViewModel
import io.github.jakubherr.gitfit.presentation.navigateToTopLevelDestination
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import io.github.jakubherr.gitfit.presentation.sharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

fun NavGraphBuilder.planningNavigation(
    navController: NavHostController,
    showSnackbar: (String) -> Unit,
) {
    fun handleError(
        error: Plan.Error?,
        scope: CoroutineScope,
        vm: PlanningViewModel,
    ) {
        if (error == null) {
            navController.popBackStack()
        } else {
            scope.launch {
                showSnackbar(error.message)
                vm.onAction(PlanAction.ErrorHandled)
            }
        }
    }

    composable<PlanOverviewRoute> {
        val vm = navController.sharedViewModel<PlanningViewModel>()

        PlanListScreenRoot(
            vm = vm,
            onCreateNewPlan = { navController.navigate(PlanCreationRoute) },
            onPlanSelected = { navController.navigate(PlanDetailRoute(it.id)) },
        )
    }

    composable<PlanDetailRoute> { backstackEntry ->
        val planViewModel = navController.sharedViewModel<PlanningViewModel>()
        val workoutViewModel = navController.sharedViewModel<WorkoutViewModel>()

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
                    if (currentWorkout == null) {
                        workoutViewModel.onAction(WorkoutAction.StartPlannedWorkout(plan, workout.idx))
                        navController.navigateToTopLevelDestination(TopLevelDestination.DASHBOARD)
                        navController.navigate(WorkoutInProgressRoute)
                    } else {
                        scope.launch {
                            showSnackbar(getString(Res.string.error_workout_in_progress))
                        }
                    }
                },
                onAction = { action ->
                    planViewModel.onAction(action)
                    if (action is PlanAction.DeletePlan) navController.popBackStack()
                    if (action is PlanAction.EditPlan) navController.navigate(PlanCreationRoute)
                },
            )
        }
    }

    composable<AddExerciseToPlanRoute> { backstackEntry ->
        val idx = backstackEntry.toRoute<AddExerciseToPlanRoute>().workoutIdx
        val planViewModel = navController.sharedViewModel<PlanningViewModel>()
        val exerciseViewModel = navController.sharedViewModel<ExerciseViewModel>()

        ExerciseListScreenRoot(
            vm = exerciseViewModel,
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = { exercise ->
                planViewModel.onAction(PlanAction.AddExercise(idx, exercise))
                navController.popBackStack()
            },
        )
    }

    composable<PlanCreationRoute> {
        val vm = navController.sharedViewModel<PlanningViewModel>()
        val scope = rememberCoroutineScope()

        PlanCreationScreen(
            vm.plan,
            onAction = { action ->
                vm.onAction(action)
                when (action) {
                    PlanAction.DiscardPlan -> navController.popBackStack()
                    PlanAction.SavePlan -> handleError(vm.error, scope, vm)
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
        val vm = navController.sharedViewModel<PlanningViewModel>()
        val scope = rememberCoroutineScope()

        PlanWorkoutCreationScreen(
            workoutPlan = vm.plan.workoutPlans[idx],
            onAction = { vm.onAction(it) },
            onAddExerciseClick = { workoutIdx ->
                navController.navigate(AddExerciseToPlanRoute(workoutIdx))
            },
            onSave = { handleError(vm.error, scope, vm) },
            onEditProgression = { block -> navController.navigate(EditProgressionRoute(idx, block.idx)) },
        )
    }

    composable<EditProgressionRoute> { backstackEntry ->
        val entry = backstackEntry.toRoute<EditProgressionRoute>()
        val vm = navController.sharedViewModel<PlanningViewModel>()
        val workout = vm.plan.workoutPlans.getOrNull(entry.workoutIdx)
        val block = workout?.blocks?.getOrNull(entry.blockIdx)

        if (block == null) {
            Text(stringResource(Res.string.error_block_not_found))
        } else {
            EditProgressionScreen(
                block,
                onCancel = { navController.popBackStack() },
                onDelete = {
                    vm.onAction(PlanAction.DeleteProgression(workout, block))
                    navController.popBackStack()
                },
                onSave = {
                    vm.onAction(PlanAction.SaveProgression(workout, block, it))
                    navController.popBackStack()
                },
            )
        }
    }
}
