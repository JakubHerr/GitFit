package io.github.jakubherr.gitfit.presentation.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

// on Android, the library actually works, so email verification can be updated
actual suspend fun checkEmailValidation(): Boolean {
    runCatching {
        Firebase.auth.currentUser
            ?.reload()
            ?.await()
    }
    return Firebase.auth.currentUser?.isEmailVerified ?: false
}
