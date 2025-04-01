package io.github.jakubherr.gitfit.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val auth: AuthRepository,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private val error = MutableStateFlow<AuthError?>(null)
    val currentUser get() = auth.currentUser

    val state: StateFlow<AuthState> = combine(auth.currentUserFlow, error, isLoading) { user, error, loading ->
        AuthState(
            user ?: User.LoggedOut,
            error,
            loading
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = AuthState(auth.currentUser, null, false),
        started = SharingStarted.WhileSubscribed(5_000L),
    )

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
        launch { auth.registerUser(email, password) }
    }

    private fun signIn(
        email: String,
        password: String,
    ) {
        launch { auth.signInUser(email, password) }
    }

    private fun signOut() {
        println("DBG: signing out ${state.value.user.id}...")
        launch { auth.signOut() }
    }

    private fun verifyEmail() {
        println("DBG: email verification requested")
        launch { auth.sendVerificationEmail() }
    }

    private fun sendPasswordResetEmail(email: String) {
        launch { auth.sendPasswordResetEmail(email) }
    }

    private fun deleteAccount(password: String) {
        println("DBG: deleting user ${state.value.user.id}")
        launch {
            auth.deleteUser(password).onFailure {
                println("DBG: Failed to delete user, cause ${it.stackTraceToString()}")
            }
        }
    }

    private fun <T> launch(block: suspend () -> Result<T>) {
        viewModelScope.launch {
            isLoading.value = true
            block().onFailure { error.value = it as AuthError }
            isLoading.value = false
        }
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
    val error: AuthError?,
    val loading: Boolean
)
