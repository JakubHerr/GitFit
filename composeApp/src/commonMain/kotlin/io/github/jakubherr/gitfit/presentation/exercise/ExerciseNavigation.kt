package io.github.jakubherr.gitfit.presentation.exercise

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.jakubherr.gitfit.presentation.AddExerciseToWorkoutRoute
import io.github.jakubherr.gitfit.presentation.CreateExerciseRoute
import io.github.jakubherr.gitfit.presentation.ExerciseDetailRoute
import io.github.jakubherr.gitfit.presentation.ExerciseListRoute
import io.github.jakubherr.gitfit.presentation.workout.WorkoutAction
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.exerciseNavigation(navController: NavHostController) {
    composable<ExerciseListRoute> {
        ExerciseListScreenRoot(
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = { /* TODO navigate to exercise detail */ },
        )
    }
    composable<AddExerciseToWorkoutRoute> { backStackEntry ->
        val route: AddExerciseToWorkoutRoute = backStackEntry.toRoute()
        val workoutViewModel: WorkoutViewModel = koinViewModel()
        ExerciseListScreenRoot(
            onCreateExerciseClick = { navController.navigate(CreateExerciseRoute) },
            onExerciseClick = { exercise ->
                workoutViewModel.onAction(WorkoutAction.AddBlock(route.workoutId, exercise.id))
                navController.popBackStack()
            },
        )
    }
    composable<ExerciseDetailRoute> {
        // TODO
    }
    composable<CreateExerciseRoute> {
        CreateExerciseScreenRoot {
            navController.popBackStack()
        }
    }
}
