package io.github.jakubherr.gitfit.presentation.auth

actual suspend fun checkEmailValidation(): Boolean {
    // Currently, it is not possible to check email validation on desktop SDK, so just continue to dashboard
    return true
}
