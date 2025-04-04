package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
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
    workoutViewModel: WorkoutViewModel,
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

    composable<AddExerciseToWorkoutRoute> {
        val currentWorkout by workoutViewModel.currentWorkout.collectAsStateWithLifecycle()

        ExerciseListScreenRoot(
            vm = exerciseViewModel,
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = { exercise ->
                currentWorkout?.let {
                    workoutViewModel.onAction(WorkoutAction.AddBlock(it, exercise))
                }
                navController.popBackStack()
            },
        )
    }

    composable<ExerciseDetailRoute> {
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
