package io.github.jakubherr.gitfit.presentation.planning

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.jakubherr.gitfit.presentation.AddExerciseToPlanRoute
import io.github.jakubherr.gitfit.presentation.PlanCreationRoute
import io.github.jakubherr.gitfit.presentation.PlanOverviewRoute
import io.github.jakubherr.gitfit.presentation.PlanningWorkoutRoute

fun NavGraphBuilder.planningGraph(
    navController: NavHostController,
    viewModel: PlanningViewModel,
) {
    composable<PlanOverviewRoute> {
        PlanOverviewScreenRoot(
            vm = viewModel,
            onCreateNewPlan = { navController.navigate(PlanCreationRoute) }
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