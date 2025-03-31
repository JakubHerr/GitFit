package io.github.jakubherr.gitfit.data.repository

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// note: all Firebase function calls MUST be in a try-catch block to handle errors and missing features in GitLive SDK
class FirebaseAuthRepository: AuthRepository {
    private val auth = Firebase.auth
    override val currentUser: User get() = auth.currentUser?.toUser() ?: User.LoggedOut
    override val currentUserFlow: Flow<User?> = auth.authStateChanged.map { it?.toUser() }

    override suspend fun registerUser(
        email: String,
        password: String,
    ): Result<User> {
        println("DBG: Registering user...")
        return runWithErrorChecking {
            val result = auth.createUserWithEmailAndPassword(email, password)
            Result.success(result.user.toUser())
        }
    }

    override suspend fun signInUser(
        email: String,
        password: String,
    ): Result<User> {
        println("DBG: Signing in user...")
        return runWithErrorChecking {
            val result = auth.signInWithEmailAndPassword(email, password)
            Result.success(result.user.toUser())
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runWithErrorChecking {
            auth.signOut()
            Result.success(Unit)
        }
    }

    override suspend fun deleteUser(password: String): Result<Unit> {
        try {
            // TODO official firebase extension deletes all data related to user, but requires pay-as-you-go Blaze plan
            // consider the number of deletes necessary to nuke all user data
            Firebase.auth.currentUser?.let { user ->
                signInUser(user.email!!, password).onFailure { return Result.failure(it) }
                user.delete()
            }
            return Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            println("DBG: Firebase auth failed: ${e.stackTraceToString()}")
            return Result.failure(AuthError.Generic)
        }
    }

    override suspend fun sendVerificationEmail(): Result<Unit> {
        return runWithErrorChecking {
            auth.currentUser?.sendEmailVerification()
            Result.success(Unit)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return runWithErrorChecking {
            auth.sendPasswordResetEmail(email)
            Result.success(Unit)
        }
    }

    private suspend fun <T> runWithErrorChecking(block: suspend () -> Result<T>): Result<T> {
        try {
            return block()
        } catch (e: FirebaseAuthWeakPasswordException) {
            println("DBG: attempt to register with password that is too weak")
            return Result.failure(AuthError.PasswordTooWeak)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            println("DBG: user entered invalid account credentials")
            return Result.failure(AuthError.InvalidCredentials)
        } catch (e: FirebaseNetworkException) {
            return Result.failure(AuthError.NoInternet)
        } catch (e: FirebaseAuthUserCollisionException) {
            return Result.failure(AuthError.EmailInUseAlready)
        }
        catch (e: FirebaseAuthException) {
            return Result.failure(AuthError.Generic)
        } catch (e: Exception) {
            println("DBG: warning! Unknown and unexpected error encountered: \n ${e.stackTraceToString()}")
            return Result.failure(AuthError.Unknown)
        }
    }
}

private fun FirebaseUser?.toUser() = User(
    this?.uid ?: "",
    this?.email ?: "",
    this != null,
    this?.isEmailVerified ?: false,
)
