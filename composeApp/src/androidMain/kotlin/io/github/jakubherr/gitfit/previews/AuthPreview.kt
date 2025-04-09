package io.github.jakubherr.gitfit.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.User
import io.github.jakubherr.gitfit.presentation.auth.AuthState
import io.github.jakubherr.gitfit.presentation.auth.LoginScreen
import io.github.jakubherr.gitfit.presentation.auth.ResetPasswordScreen
import io.github.jakubherr.gitfit.presentation.auth.VerifyEmailScreen
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview
@Composable
private fun LoginScreenPreview() {
    GitFitTheme {
        LoginScreen(
            state =
                AuthState(
                    user = User.LoggedOut,
                    error = null,
                    loading = false,
                ),
        )
    }
}

@Preview
@Composable
private fun ResetPasswordScreenPreview() {
    GitFitTheme {
        ResetPasswordScreen(
            state =
            AuthState(
                User.LoggedOut,
                error = null,
                loading = true,
            ),
            onResetPassword = { }
        )
    }
}

@Preview
@Composable
private fun VerifyEmailScreenPreview() {
    GitFitTheme {
        VerifyEmailScreen()
    }
}
