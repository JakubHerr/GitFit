package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User
    val currentUserFlow: Flow<User?>

    suspend fun registerUser(email: String, password: String): Result<User>

    suspend fun signInUser(email: String, password: String): Result<User>

    suspend fun signOut(): Result<Unit>

    suspend fun deleteUser(password: String): Result<Unit>

    suspend fun sendVerificationEmail(): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}

sealed class AuthError : Exception() {
    object PasswordTooWeak : AuthError()
    object EmailInUseAlready : AuthError()
    object InvalidCredentials : AuthError()
    object FailedToSendEmail : AuthError()
    object NoInternet : AuthError()
    object Generic: AuthError()
    object Unknown: AuthError()
}