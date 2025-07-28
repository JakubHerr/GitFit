package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.add_edit_measurement_route
import gitfit.composeapp.generated.resources.create_exercise_route
import gitfit.composeapp.generated.resources.exercise_detail_route
import gitfit.composeapp.generated.resources.measurement_history_route
import gitfit.composeapp.generated.resources.plan_creation_route
import gitfit.composeapp.generated.resources.plan_detail_route
import gitfit.composeapp.generated.resources.planing_workout_route
import gitfit.composeapp.generated.resources.reset_password_route
import gitfit.composeapp.generated.resources.select_exercise_route
import gitfit.composeapp.generated.resources.verify_email_route
import gitfit.composeapp.generated.resources.workout_detail_route
import gitfit.composeapp.generated.resources.workout_history_route
import gitfit.composeapp.generated.resources.workout_in_progress_route
import org.jetbrains.compose.resources.stringResource

// returns a pair with destination name and a boolean for disabling back button on critical operations (plan creation)
@Composable
fun NavHostController.destinationSettings(): Pair<String?, Boolean> {
    val destination = currentBackStackEntryAsState().value?.destination ?: return null to false

    return when {
        destination.hasRoute<ResetPasswordRoute>() -> stringResource(Res.string.reset_password_route) to true
        destination.hasRoute<PlanCreationRoute>() -> stringResource(Res.string.plan_creation_route) to false
        destination.hasRoute<ExerciseListRoute>() -> stringResource(Res.string.select_exercise_route) to true
        destination.hasRoute<AddExerciseToWorkoutRoute>() -> stringResource(Res.string.select_exercise_route) to true
        destination.hasRoute<AddExerciseToPlanRoute>() -> stringResource(Res.string.select_exercise_route) to true
        destination.hasRoute<WorkoutHistoryRoute>() -> stringResource(Res.string.workout_history_route) to true
        destination.hasRoute<MeasurementAddEditRoute>() -> stringResource(Res.string.add_edit_measurement_route) to true
        destination.hasRoute<WorkoutInProgressRoute>() -> stringResource(Res.string.workout_in_progress_route) to true
        destination.hasRoute<WorkoutDetailRoute>() -> stringResource(Res.string.workout_detail_route) to true
        destination.hasRoute<PlanningWorkoutRoute>() -> stringResource(Res.string.planing_workout_route) to true
        destination.hasRoute<PlanDetailRoute>() -> stringResource(Res.string.plan_detail_route) to true
        destination.hasRoute<CreateExerciseRoute>() -> stringResource(Res.string.create_exercise_route) to false
        destination.hasRoute<ExerciseDetailRoute>() -> stringResource(Res.string.exercise_detail_route) to true
        destination.hasRoute<VerifyEmailRoute>() -> stringResource(Res.string.verify_email_route) to false
        destination.hasRoute<MeasurementHistoryRoute>() -> stringResource(Res.string.measurement_history_route) to true
        else -> null to false
    }
}

@Composable
fun NavHostController.currentTopLevelDestination(): TopLevelDestination? {
    val destination = currentBackStackEntryAsState().value?.destination
    return TopLevelDestination.entries.firstOrNull { destination?.hasRoute(it.route) == true }
}

fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
    val route: Any =
        when (destination) {
            TopLevelDestination.DASHBOARD -> DashboardRoute
            TopLevelDestination.HISTORY -> HistoryRoute
            TopLevelDestination.MEASUREMENT -> MeasurementRoute
            TopLevelDestination.PLAN -> PlanOverviewRoute
            TopLevelDestination.SETTINGS -> SettingsRoute
        }
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
