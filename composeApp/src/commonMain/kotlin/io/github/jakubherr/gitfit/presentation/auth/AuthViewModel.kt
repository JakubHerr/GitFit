package io.github.jakubherr.gitfit.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.AuthRepository
import io.github.jakubherr.gitfit.domain.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val auth: AuthRepository,
) : ViewModel() {
    private val _state =
        auth.currentUserFlow.map {
            AuthState(
                it ?: User.LoggedOut,
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = AuthState(auth.currentUser),
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
        viewModelScope.launch { auth.registerUser(email, password) }
    }

    private fun signIn(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            auth.signInUser(email, password)
        }
    }

    private fun signOut() {
        println("DBG: signing out ${state.value.user.userId}...")
        viewModelScope.launch { auth.signOut() }
    }

    private fun verifyEmail() {
        println("DBG: email verification requested")
        viewModelScope.launch {
            auth.sendVerificationEmail()
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch { auth.sendPasswordResetEmail(email) }
    }

    private fun deleteAccount(password: String) {
        println("DBG: deleting user ${state.value.user.userId}")
        viewModelScope.launch { auth.deleteUser(password) }
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

data class AuthState(
    val user: User,
    // TODO error state holder
    // TODO loading state holder
)