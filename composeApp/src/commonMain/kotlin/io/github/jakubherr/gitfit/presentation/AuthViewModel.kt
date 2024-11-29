package io.github.jakubherr.gitfit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.data.Supabase
import kotlinx.coroutines.launch

class AuthViewModel(private val supabase: Supabase): ViewModel() {
    fun register(email: String, password: String) {
        viewModelScope.launch { supabase.registerUser(email, password) }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch { supabase.signIn(email, password) }
    }
}
