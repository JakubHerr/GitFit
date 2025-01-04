package io.github.jakubherr.gitfit.presentation.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jakubherr.gitfit.presentation.LoginRoute
import io.github.jakubherr.gitfit.presentation.OnboardingRoute
import io.github.jakubherr.gitfit.presentation.RegisterRoute

fun NavGraphBuilder.authGraph() {
    composable<LoginRoute> {
        LoginScreenRoot { }
    }

    composable<RegisterRoute> { /* TODO */ }

    composable<OnboardingRoute> { /* TODO */ }
}
