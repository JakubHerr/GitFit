package io.github.jakubherr.gitfit.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.data.Supabase
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val supabase: Supabase) : ViewModel() {
    private val _state = MutableStateFlow(
        AuthState(
            "",
            supabase.auth.sessionStatus.value is SessionStatus.Authenticated
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect {
                when(it) {
                    is SessionStatus.Authenticated -> {
                        println("Received new authenticated session.")
                        _state.value = _state.value.copy(email = it.session.user?.email ?: "unknown", loggedIn = true)

                        when(it.source) { // Check the source of the session
                            is SessionSource.External -> TODO()
                            is SessionSource.Refresh -> println("User refreshed")
                            is SessionSource.SignIn -> println("User just signed in")
                            is SessionSource.SignUp -> TODO("Sign up should trigger Onboarding")
                            is SessionSource.Storage -> println("User login was saved in storage")
                            is SessionSource.Unknown -> TODO()
                            is SessionSource.UserChanged -> TODO()
                            is SessionSource.UserIdentitiesChanged -> TODO()
                            is SessionSource.AnonymousSignIn -> TODO()
                        }
                    }
                    is SessionStatus.Initializing -> println("Initializing")

                    is SessionStatus.RefreshFailure -> println("Refresh failure ${it.cause}") // Either a network error or a internal server error

                    is SessionStatus.NotAuthenticated -> {
                        if(it.isSignOut) {
                            println("User signed out")
                            _state.value = state.value.copy(email = "", loggedIn = false)
                        } else {
                            println("User not signed in")
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: AuthAction) {
        when(action) {
            is AuthAction.Register -> register(action.email, action.password)
            is AuthAction.SignIn -> signIn(action.email, action.password)
            AuthAction.SignOut -> signOut()
        }
    }

    private fun register(
        email: String,
        password: String,
    ) {
        viewModelScope.launch { supabase.registerUser(email, password) }
    }

    private fun signIn(
        email: String,
        password: String,
    ) {
        viewModelScope.launch { supabase.signIn(email, password) }
    }

    private fun signOut() {
        viewModelScope.launch { supabase.signOut() }
    }
}

sealed interface AuthAction {
    class SignIn(val email: String, val password: String) : AuthAction
    class Register(val email: String, val password: String) : AuthAction
    object SignOut : AuthAction
}
