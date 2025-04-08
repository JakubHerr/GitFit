package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.User
import io.github.jakubherr.gitfit.ui.theme.GitFitTheme

@Preview
@Composable
private fun LoginScreenPreview() {
    GitFitTheme {
        LoginScreen(
            state = AuthState(
                user = User.LoggedOut,
                error = null,
                loading = false
            )
        )
    }
}
