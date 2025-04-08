package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.jakubherr.gitfit.presentation.AuthGraphRoute
import io.github.jakubherr.gitfit.presentation.ResetPasswordRoute
import io.github.jakubherr.gitfit.presentation.LoginRoute
import io.github.jakubherr.gitfit.presentation.OnboardingRoute
import io.github.jakubherr.gitfit.presentation.RegisterRoute

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute
    ) {
        composable<RegisterRoute> { /* TODO */ }

        composable<LoginRoute> {
            LoginScreenRoot(
                snackbarHostState = snackbarHostState,
                onForgotPassword = { navController.navigate(ResetPasswordRoute) }
            )
        }

        composable<ResetPasswordRoute> {
            ResetPasswordScreenRoot(
                snackbarHostState = snackbarHostState,
                onBack = { navController.popBackStack() }
            )
        }

        composable<OnboardingRoute> { /* TODO */ }
    }
}
