package io.github.jakubherr.gitfit.presentation.exercise

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.jakubherr.gitfit.presentation.AddExerciseToWorkoutRoute
import io.github.jakubherr.gitfit.presentation.CreateExerciseRoute
import io.github.jakubherr.gitfit.presentation.ExerciseDetailRoute
import io.github.jakubherr.gitfit.presentation.ExerciseListRoute
import io.github.jakubherr.gitfit.presentation.graph.GraphViewModel
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.exerciseNavigation(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel,
) {
    composable<ExerciseListRoute> {
        ExerciseListScreenRoot(
            vm = exerciseViewModel,
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = {
                exerciseViewModel.onAction(ExerciseAction.SelectExercise(it))
                navController.navigate(ExerciseDetailRoute(it.id, it.isCustom)) },
        )
    }

    composable<AddExerciseToWorkoutRoute> { backStackEntry ->
        val route: AddExerciseToWorkoutRoute = backStackEntry.toRoute()
        val workoutViewModel: WorkoutViewModel = koinViewModel()

        ExerciseListScreenRoot(
            vm = exerciseViewModel,
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = { exercise ->
                workoutViewModel.onAction(WorkoutAction.AddBlock(route.workoutId, exercise))
                navController.popBackStack()
            },
        )
    }

    composable<ExerciseDetailRoute> { backstackEntry ->
        val graphVm: GraphViewModel = koinViewModel()

        ExerciseDetailScreenRoot(
            graphViewModel = graphVm,
            exerciseViewModel = exerciseViewModel,
            onBack = { navController.popBackStack() }
        )
    }

    composable<CreateExerciseRoute> {
        ExerciseCreateScreenRoot(
            onExerciseCreated = {
                exerciseViewModel.onAction(ExerciseAction.CreateExercise(it))
                navController.popBackStack()
            },
            onCancel = { navController.popBackStack() }
        )
    }
}
