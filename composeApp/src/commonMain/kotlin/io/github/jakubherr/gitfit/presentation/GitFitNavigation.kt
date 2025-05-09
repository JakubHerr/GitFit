package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.account_deleted
import gitfit.composeapp.generated.resources.password_changed
import gitfit.composeapp.generated.resources.password_reset_sent
import gitfit.composeapp.generated.resources.verification_email_sent
import io.github.jakubherr.gitfit.presentation.auth.AuthAction
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.auth.VerifyEmailScreenRoot
import io.github.jakubherr.gitfit.presentation.auth.authGraph
import io.github.jakubherr.gitfit.presentation.auth.getMessage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

// This is the top-level navigation component and contains destinations for the entire application
@Composable
fun GitFitNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    showSnackbar: (String) -> Unit,
) {
    // this viewmodel is global because it is needed for the entire app lifecycle
    // it checks the user auth state and handles snackbars for authentication errors and events
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val finishedAction by authViewModel.finishedAction.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState.error, finishedAction) {
        val error = authState.error
        val action = finishedAction

        if (error != null) {
            scope.launch {
                showSnackbar(error.getMessage())
                authViewModel.onAction(AuthAction.ErrorHandled)
            }
        }

        if (action != null) {
            scope.launch {
                when (action) {
                    is AuthAction.VerifyEmail -> showSnackbar(getString(Res.string.verification_email_sent))
                    is AuthAction.RequestPasswordReset -> showSnackbar("${getString(Res.string.password_reset_sent)} ${action.email}")
                    is AuthAction.DeleteAccount -> showSnackbar(getString(Res.string.account_deleted))
                    is AuthAction.ChangePassword -> showSnackbar(getString(Res.string.password_changed))
                    else -> { }
                }
                authViewModel.onAction(AuthAction.ActionHandled)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination =
            when {
                authState.user.loggedIn && authState.user.emailVerified -> LoggedInRoute
                authState.user.loggedIn && !authState.user.emailVerified -> VerifyEmailRoute
                else -> AuthGraphRoute
            },
        modifier = modifier,
    ) {
        authGraph(navController, authViewModel)
        loggedInNavigation(navController, showSnackbar, authViewModel)
        composable<VerifyEmailRoute> {
            VerifyEmailScreenRoot(
                authViewModel,
                onSkip = {
                    navController.navigate(LoggedInRoute) {
                        popUpTo(VerifyEmailRoute) { inclusive = true }
                    }
                },
            )
        }
    }
}
