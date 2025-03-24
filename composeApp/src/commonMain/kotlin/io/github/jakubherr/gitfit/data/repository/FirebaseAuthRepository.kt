package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import io.github.jakubherr.gitfit.domain.AuthError
import io.github.jakubherr.gitfit.domain.AuthRepository
import io.github.jakubherr.gitfit.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// note: all Firebase function calls MUST be in a try-catch block to handle errors and missing features in GitLive SDK
class FirebaseAuthRepository: AuthRepository {
    private val auth = Firebase.auth

    override val currentUser: User
        get() = auth.currentUser?.toUser() ?: User.LoggedOut
    override val currentUserFlow: Flow<User?> = auth.authStateChanged.map { it?.toUser() }

    override suspend fun registerUser(
        email: String,
        password: String,
    ): Result<User> {
        println("DBG: Registering user...")
        try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            return Result.success(result.user.toUser())
        } catch (e: FirebaseAuthWeakPasswordException) {
            println("DBG: attempt to register with password that is too weak")
            return Result.failure(AuthError.PasswordTooWeak)
        }
        catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
            return Result.failure(AuthError.Generic)
        }
    }

    override suspend fun signInUser(
        email: String,
        password: String,
    ): Result<User> {
        println("DBG: Signing in user...")
        try {
            val result = auth.signInWithEmailAndPassword(email, password)
            return Result.success(result.user.toUser())
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            println("DBG: user entered invalid account credentials")
            return Result.failure(AuthError.InvalidCredentials)
        } catch (e: FirebaseNetworkException) {
            return Result.failure(AuthError.NoInternet)
        } catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
            return Result.failure(AuthError.Generic)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        try {
            auth.signOut()
            return Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
            return Result.failure(AuthError.Generic)
        }
    }

    override suspend fun deleteUser(password: String): Result<Unit> {
        try {
            // TODO official firebase extension deletes all data related to user, but requires pay-as-you-go Blaze plan
            // consider the number of deletes necessary to nuke all user data
            Firebase.auth.currentUser?.let { user ->
                signInUser(user.email!!, password)
                user.delete()
            }
            return Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            println("DBG: Firebase auth failed: ${e.stackTraceToString()}")
            return Result.failure(AuthError.Generic)
        }
    }

    override suspend fun sendVerificationEmail(): Result<Unit> {
        try {
            auth.currentUser?.sendEmailVerification()
            return Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            return Result.failure(AuthError.FailedToSendEmail)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        try {
            auth.sendPasswordResetEmail(email)
            return Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            return Result.failure(AuthError.FailedToSendEmail)
        }
    }
}

private fun FirebaseUser?.toUser() = User(
    this?.uid ?: "",
    this?.email ?: "",
    this != null,
    this?.isEmailVerified ?: false,
)
