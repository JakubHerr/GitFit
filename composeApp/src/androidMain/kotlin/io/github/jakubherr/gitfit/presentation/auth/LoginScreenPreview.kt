package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.User

@Preview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            state = AuthState(
                user = User.LoggedOut,
                error = null,
                loading = false
            )
        )
    }
}
