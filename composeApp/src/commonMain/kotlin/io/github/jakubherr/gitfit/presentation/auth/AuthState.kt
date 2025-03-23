package io.github.jakubherr.gitfit.presentation.auth

data class AuthState(
    val uid: String,
    val loggedIn: Boolean,
    val emailVerified: Boolean,
    // TODO support anonymous user
)
