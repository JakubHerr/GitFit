package io.github.jakubherr.gitfit.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        GitFitTheme {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            val destination = navController.currentBackStackEntryAsState().value?.destination
            val topLevelDestination = TopLevelDestination.entries.firstOrNull { destination?.hasRoute(it.route) == true }

            GitFitTheme {
                GitFitNavScaffold(
                    currentDestination = topLevelDestination,
                    onDestinationClicked = {
                        navController.navigateToTopLevelDestination(it)
                    },
                ) {
                    Scaffold(
                        topBar = {
                            val settings = navController.destinationSettings()
                            GitFitTopAppBar(settings.first, settings.second) {
                                navController.popBackStack()
                            }
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                    ) { padding ->
                        GitFitNavHost(
                            navController,
                            modifier = Modifier.padding(padding),
                            showSnackbar = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitFitTopAppBar(
    title: String?,
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { },
) {
    AnimatedVisibility(
        title != null,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
    ) {
        CenterAlignedTopAppBar(
            title = { Text(title ?: "") },
            navigationIcon = {
                if (showBackButton) {
                    IconButton({ if (title != null) onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                }
            },
        )
    }
}

// returns a pair with destination name and a boolean for disabling back button on critical operations (plan creation)
@Composable
private fun NavHostController.destinationSettings(): Pair<String?, Boolean> {
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

fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
    val route: Any =
        when (destination) {
            TopLevelDestination.DASHBOARD -> DashboardRoute
            TopLevelDestination.HISTORY -> HistoryRoute
            TopLevelDestination.MEASUREMENT -> MeasurementRoute
            TopLevelDestination.PLAN -> PlanOverviewRoute
            TopLevelDestination.PROFILE -> SettingsRoute
        }
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
