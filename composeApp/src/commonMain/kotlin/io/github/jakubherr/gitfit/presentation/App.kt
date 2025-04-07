package io.github.jakubherr.gitfit.presentation

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
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.weight
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
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
                    topBar = {
                        if (topLevelDestination == null) {
                            GitFitTopAppBar(Res.string.weight) {
                                navController.popBackStack()
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    GitFitNavHost(
                        navController,
                        modifier = Modifier.padding(padding),
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitFitTopAppBar(
    titleRes: StringResource,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { }, // TODO restrict back navigation on creation screens
    // TODO maybe hack this by creating a function that explicitly maps routes to titles. If title is null -> no top bar
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(titleRes)) },
        navigationIcon = {
            IconButton(onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
            }
        }
    )
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
