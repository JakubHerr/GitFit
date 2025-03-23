package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuthEmailException
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow

// note: all Firebase function calls MUST be in a try-catch block to handle errors and missing features in GitLive SDK
class FirebaseAuthRepository {
    // TODO add support for anonymous user
    private val auth = Firebase.auth

    suspend fun registerUser(
        email: String,
        password: String,
    ) {
        println("Registering user...")
        try {
            val result = auth.createUserWithEmailAndPassword(email, password)
        } catch (e: FirebaseAuthWeakPasswordException) {
            // TODO notify in UI
            println("DBG: attempt to register with password that is too weak")
        }
        catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
        }
    }

    suspend fun signIn(
        email: String,
        password: String,
    ) {
        println("Signing in user...")
        try {
            val result = auth.signInWithEmailAndPassword(email, password)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // TODO notify in UI
            println("DBG: user entered invalid account credentials")
        } catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
        }
    }

    suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
        }
    }

    suspend fun deleteUser(password: String) {
        try {
            // TODO official firebase extension deletes all data related to user, but requires pay-as-you-go Blaze plan
            // consider the number of deletes necessary to nuke all user data
            //  how will it work for an anonymous user?

            Firebase.auth.currentUser?.let { user ->
                user.reauthenticate(EmailAuthProvider.credential(email = user.email!!, password = password))
                user.delete()
            }

        } catch (e: FirebaseAuthException) {
            println("DBG: Firebase auth failed: ${e.stackTraceToString()}")
        }
    }

    suspend fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    val currentUserFlow: Flow<FirebaseUser?> = auth.authStateChanged
}
