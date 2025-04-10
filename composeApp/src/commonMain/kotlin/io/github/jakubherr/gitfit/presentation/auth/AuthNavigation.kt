package io.github.jakubherr.gitfit.presentation.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.jakubherr.gitfit.presentation.AuthGraphRoute
import io.github.jakubherr.gitfit.presentation.LoginRoute
import io.github.jakubherr.gitfit.presentation.OnboardingRoute
import io.github.jakubherr.gitfit.presentation.ResetPasswordRoute

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute,
    ) {
        composable<LoginRoute> {
            LoginScreenRoot(
                authViewModel,
                onForgotPassword = { navController.navigate(ResetPasswordRoute) },
            )
        }

        composable<ResetPasswordRoute> {
            ResetPasswordScreenRoot(
                authViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable<OnboardingRoute> { /* TODO */ }
    }
}
