package io.github.jakubherr.gitfit.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebase: FirebaseAuthRepository,
) : ViewModel() {
    private val _state =
        firebase.currentUserFlow.map {
            AuthState(
                it?.uid ?: "",
                it != null,
                it?.isEmailVerified ?: false,
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = AuthState("", false, false),
            started = SharingStarted.WhileSubscribed(5_000L),
        )
    val state = _state

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.SignIn -> signIn(action.email, action.password)
            is AuthAction.Register -> register(action.email, action.password)
            is AuthAction.VerifyEmail -> verifyEmail()
            is AuthAction.RequestPasswordReset -> sendPasswordResetEmail(action.email)
            is AuthAction.SignOut -> signOut()
            is AuthAction.DeleteAccount -> deleteAccount(action.password)
        }
    }

    private fun register(
        email: String,
        password: String,
    ) {
        viewModelScope.launch { firebase.registerUser(email, password) }
    }

    private fun signIn(
        email: String,
        password: String,
    ) {
        viewModelScope.launch { firebase.signIn(email, password) }
    }

    private fun signOut() {
        println("DBG: signing out ${state.value.uid}...")
        viewModelScope.launch { firebase.signOut() }
    }

    private fun verifyEmail() {
        println("DBG: email verification requested")
        viewModelScope.launch {
            firebase.sendEmailVerification()
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch { firebase.sendPasswordResetEmail(email) }
    }

    private fun deleteAccount(password: String) {
        println("DBG: deleting user ${state.value.uid}")
        viewModelScope.launch { firebase.deleteUser(password) }
    }
}

sealed interface AuthAction {
    class SignIn(val email: String, val password: String) : AuthAction
    class Register(val email: String, val password: String) : AuthAction
    class RequestPasswordReset(val email: String) : AuthAction
    class DeleteAccount(val password: String) : AuthAction

    object SignOut : AuthAction
    object VerifyEmail : AuthAction
}
