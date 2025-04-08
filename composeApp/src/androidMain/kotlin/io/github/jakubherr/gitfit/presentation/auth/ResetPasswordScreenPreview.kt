package io.github.jakubherr.gitfit.presentation.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jakubherr.gitfit.domain.model.User

@Preview
@Composable
private fun ResetPasswordScreenPreview() {
    MaterialTheme {
        ResetPasswordScreen(
            state = AuthState(
                User.LoggedOut,
                error = null,
                loading = false
            )
        )
    }
}