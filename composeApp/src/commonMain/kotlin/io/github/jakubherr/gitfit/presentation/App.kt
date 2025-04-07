package io.github.jakubherr.gitfit.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        // TODO add custom app theme
        MaterialTheme {
            // TODO add application-level UI state holder

            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            val destination = navController.currentBackStackEntryAsState().value?.destination
            val topLevelDestination = TopLevelDestination.entries.firstOrNull { destination?.hasRoute(it.route) == true }

            GitFitNavScaffold(
                currentDestination = topLevelDestination,
                onDestinationClicked = {
                    navController.navigateToTopLevelDestination(it)
                },
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    Column(modifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
                        GitFitNavHost(navController, snackbarHostState = snackbarHostState)
                    }
                }
            }
        }
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
        // popUpTo(graph.startDestinationId) TODO fix
        launchSingleTop = true
    }
}
