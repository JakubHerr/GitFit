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
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = AuthState("", false),
            started = SharingStarted.WhileSubscribed(5_000L),
        )
    val state = _state

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.Register -> register(action.email, action.password)
            is AuthAction.SignIn -> signIn(action.email, action.password)
            AuthAction.SignOut -> signOut()
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
}

sealed interface AuthAction {
    class SignIn(val email: String, val password: String) : AuthAction

    class Register(val email: String, val password: String) : AuthAction

    object SignOut : AuthAction
}
