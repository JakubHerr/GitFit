package io.github.jakubherr.gitfit.presentation

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext

// This is the shared entry point for both the desktop and mobile application
@Composable
fun App() {
    KoinContext {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        GitFitTheme {
            GitFitNavScaffold(
                currentDestination = navController.currentTopLevelDestination(),
                onDestinationClicked = {
                    navController.navigateToTopLevelDestination(it)
                },
            ) {
                Scaffold(
                    modifier = Modifier.imePadding(),
                    topBar = {
                        val settings = navController.destinationSettings()
                        GitFitTopAppBar(settings.first, settings.second) {
                            navController.popBackStack()
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { padding ->
                    GitFitNavigation(
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
