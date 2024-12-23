package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FirebaseAuthRepository {
    // TODO add Google SSO (maybe android only)
    // TODO add support for anonymous user
    // TODO add error message for actions unsupported on desktop
    private val auth = Firebase.auth

    suspend fun registerUser(email: String, password: String) {
        println("Registering user...")
        try {
            val result = auth.createUserWithEmailAndPassword(email, password)
        } catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
        }
    }

    suspend fun signIn(email: String, password: String) {
        println("Signing in user...")
        try {
            val result = auth.signInWithEmailAndPassword(email, password)
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

    suspend fun deleteUser() {
        try {
            // TODO official firebase extension deletes all data related to user, but requires pay-as-you-go Blaze plan
                // consider the number of deletes necessary to nuke all user data
            //  how will it work for an anonymous user?
            Firebase.auth.currentUser?.delete()
        } catch (e: FirebaseAuthException) {
            println("Firebase auth failed: ${e.stackTraceToString()}")
        }
    }

    fun hasUser() = auth.currentUser != null

    val currentUser: Flow<FirebaseUser?> = auth.authStateChanged

    suspend fun observeUser() {
        withContext(Dispatchers.IO) {
            currentUser.collect { curr ->
                if (curr == null) println("DBG: User not logged in")
                else println(curr.toString())
            }
        }
    }
}
