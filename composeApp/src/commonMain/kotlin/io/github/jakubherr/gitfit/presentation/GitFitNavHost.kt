package io.github.jakubherr.gitfit.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.auth.VerifyEmailScreenRoot
import io.github.jakubherr.gitfit.presentation.auth.authGraph
import io.github.jakubherr.gitfit.presentation.workout.loggedInGraph
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GitFitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    val authViewModel: AuthViewModel = koinViewModel()
    val auth by authViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(auth) {
        println("DBG: auth state is ${auth.user.loggedIn}")
    }

    NavHost(
        navController = navController,
        startDestination = when {
            auth.user.loggedIn && auth.user.emailVerified -> LoggedInRoute
            auth.user.loggedIn && !auth.user.emailVerified -> VerifyEmailRoute
            else -> AuthGraphRoute
        },
        modifier = modifier,
    ) {
        authGraph(navController)
        loggedInGraph(navController, snackbarHostState)
        composable<VerifyEmailRoute> { VerifyEmailScreenRoot { navController.navigate(DashboardRoute) } }
    }
}
