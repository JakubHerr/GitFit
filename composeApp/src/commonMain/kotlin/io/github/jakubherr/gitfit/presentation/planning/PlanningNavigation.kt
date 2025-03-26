package io.github.jakubherr.gitfit.presentation.planning

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.jakubherr.gitfit.presentation.AddExerciseToPlanRoute
import io.github.jakubherr.gitfit.presentation.CreateExerciseRoute
import io.github.jakubherr.gitfit.presentation.PlanCreationRoute
import io.github.jakubherr.gitfit.presentation.PlanDetailRoute
import io.github.jakubherr.gitfit.presentation.PlanOverviewRoute
import io.github.jakubherr.gitfit.presentation.PlanningWorkoutRoute
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseListScreenRoot

fun NavGraphBuilder.planningGraph(
    navController: NavHostController,
    viewModel: PlanningViewModel,
) {
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

        userPlan?.let { plan ->
            PlanDetailScreen(
                plan
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
        PlanCreationScreen(
            viewModel.plan,
            onAction = { viewModel.onAction(it) },
            onWorkoutSelected = { workoutIdx ->
                navController.navigate(PlanningWorkoutRoute(workoutIdx))
            },
            onFinished = { navController.popBackStack() }
        )
    }

    composable<PlanningWorkoutRoute> { backstackEntry ->
        val idx = backstackEntry.toRoute<PlanningWorkoutRoute>().workoutIdx

        PlanWorkoutDetailScreen(
            workout = viewModel.plan.workouts[idx],
            onAction = { viewModel.onAction(it) },
            onAddExerciseClick = { workoutIdx ->
                navController.navigate(AddExerciseToPlanRoute(workoutIdx))
            }
        )
    }
}