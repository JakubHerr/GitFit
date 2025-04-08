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
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        // TODO add custom app theme
        MaterialTheme {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            val destination = navController.currentBackStackEntryAsState().value?.destination
            val topLevelDestination = TopLevelDestination.entries.firstOrNull { destination?.hasRoute(it.route) == true }

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
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    GitFitNavHost(
                        navController,
                        modifier = Modifier.padding(padding),
                        showSnackbar = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
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
            }
        )
    }
}

// returns a pair with destination name and a boolean for disabling back button on critical operations (plan creation)
@Composable
private fun NavHostController.destinationSettings(): Pair<String?, Boolean> {
    val destination = currentBackStackEntryAsState().value?.destination ?: return null to false

    // TODO string resources
    return when {
        destination.hasRoute<ResetPasswordRoute>() -> "Reset password" to true
        destination.hasRoute<PlanCreationRoute>() -> "Create or edit plan" to false
        destination.hasRoute<ExerciseListRoute>() -> "Select exercise" to true
        destination.hasRoute<AddExerciseToWorkoutRoute>() -> "Select exercise" to true
        destination.hasRoute<AddExerciseToPlanRoute>() -> "Select exercise" to true
        destination.hasRoute<WorkoutHistoryRoute>() -> "Select workout" to true
        destination.hasRoute<MeasurementAddEditRoute>() -> "Add or edit measurement" to true
        destination.hasRoute<WorkoutInProgressRoute>() -> "Record workout" to true
        destination.hasRoute<WorkoutDetailRoute>() -> "Workout record" to true
        destination.hasRoute<PlanningWorkoutRoute>() -> "Edit workout" to true
        destination.hasRoute<PlanDetailRoute>() -> "Workout plan" to true
        destination.hasRoute<CreateExerciseRoute>() -> "Create exercise" to false
        destination.hasRoute<ExerciseDetailRoute>() -> "Exercise detail" to true
        destination.hasRoute<VerifyEmailRoute>() -> "Verify email" to false
        else -> null to false
    }
}

private fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
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
