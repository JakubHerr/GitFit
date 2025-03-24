package io.github.jakubherr.gitfit.presentation.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.jakubherr.gitfit.presentation.DashboardRoute
import io.github.jakubherr.gitfit.presentation.ResetPasswordRoute
import io.github.jakubherr.gitfit.presentation.LoginRoute
import io.github.jakubherr.gitfit.presentation.OnboardingRoute
import io.github.jakubherr.gitfit.presentation.RegisterRoute
import io.github.jakubherr.gitfit.presentation.VerifyEmailRoute

fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    composable<RegisterRoute> { /* TODO */ }

    composable<LoginRoute> {
        LoginScreenRoot(
            onForgotPassword = { navController.navigate(ResetPasswordRoute) }
        )
    }

    composable<VerifyEmailRoute> { VerifyEmailScreenRoot { navController.navigate(DashboardRoute) } }

    composable<ResetPasswordRoute> { ResetPasswordScreenRoot() }

    composable<OnboardingRoute> { /* TODO */ }
}
