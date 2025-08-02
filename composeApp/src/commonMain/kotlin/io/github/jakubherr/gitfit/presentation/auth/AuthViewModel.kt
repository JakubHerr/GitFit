package io.github.jakubherr.gitfit.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.model.User
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.domain.repository.ExerciseRepository
import io.github.jakubherr.gitfit.domain.repository.MeasurementRepository
import io.github.jakubherr.gitfit.domain.repository.PlanRepository
import io.github.jakubherr.gitfit.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository,
    private val measurementRepository: MeasurementRepository,
    private val planRepository: PlanRepository,
    private val exerciseRepository: ExerciseRepository,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private val error = MutableStateFlow<AuthError?>(null)

    // this flow is for notifying UI of successfully finished actions like password reset and verification sent
    private val _finishedAction = MutableStateFlow<AuthAction?>(null)
    val finishedAction: StateFlow<AuthAction?> = _finishedAction

    val currentUser get() = authRepository.currentUser

    val state: StateFlow<AuthState> =
        combine(authRepository.currentUserFlow, error, isLoading) { user, error, loading ->
            AuthState(
                user ?: User.LoggedOut,
                error,
                loading,
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = AuthState(authRepository.currentUser, null, false),
            started = SharingStarted.WhileSubscribed(5_000L),
        )

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.SignIn -> signIn(action.email, action.password)
            is AuthAction.Register -> register(action.email, action.password)
            is AuthAction.VerifyEmail -> verifyEmail()
            is AuthAction.RequestPasswordReset -> sendPasswordResetEmail(action.email)
            is AuthAction.SignOut -> signOut()
            is AuthAction.ChangePassword -> changePassword(action.oldPassword, action.newPassword)
            is AuthAction.DeleteAccount -> deleteAccount(action.password)
            is AuthAction.ErrorHandled -> error.value = null
            is AuthAction.ActionHandled -> _finishedAction.value = null
        }
    }

    private fun register(
        email: String,
        password: String,
    ) {
        launch { authRepository.registerUser(email, password) }
    }

    private fun signIn(
        email: String,
        password: String,
    ) {
        launch { authRepository.signInUser(email, password) }
    }

    private fun signOut() {
        launch { authRepository.signOut() }
    }

    private fun verifyEmail() {
        launch { authRepository.sendVerificationEmail().onSuccess { _finishedAction.value = AuthAction.VerifyEmail } }
    }

    private fun sendPasswordResetEmail(email: String) {
        launch {
            authRepository
                .sendPasswordResetEmail(email)
                .onSuccess { _finishedAction.value = AuthAction.RequestPasswordReset(email) }
        }
    }

    private fun changePassword(
        oldPassword: String,
        newPassword: String,
    ) {
        launch {
            authRepository
                .changePassword(oldPassword, newPassword)
                .onSuccess { _finishedAction.value = AuthAction.ChangePassword(oldPassword, newPassword) }
        }
    }

    private fun deleteAccount(password: String) {
        launch {
            authRepository
                .deleteUser(password) { userId ->
                    // nuke all user workouts
                    workoutRepository.deleteAllWorkouts(userId).onFailure { return@deleteUser Result.failure(it) }
                    // nuke all user plans
                    planRepository.deleteAllCustomPlans(userId).onFailure { return@deleteUser Result.failure(it) }
                    // nuke all user measurements
                    measurementRepository.deleteAllMeasurements(userId).onFailure { return@deleteUser Result.failure(it) }
                    // nuke all user custom exercises
                    exerciseRepository.removeAllCustomExercises(userId).onFailure { return@deleteUser Result.failure(it) }
                }.onSuccess {
                    _finishedAction.value = AuthAction.DeleteAccount("")
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
    class SignIn(
        val email: String,
        val password: String,
    ) : AuthAction

    class Register(
        val email: String,
        val password: String,
    ) : AuthAction

    class RequestPasswordReset(
        val email: String,
    ) : AuthAction

    class ChangePassword(
        val oldPassword: String,
        val newPassword: String,
    ) : AuthAction

    class DeleteAccount(
        val password: String,
    ) : AuthAction

    object SignOut : AuthAction

    object VerifyEmail : AuthAction

    object ErrorHandled : AuthAction

    object ActionHandled : AuthAction
}

data class AuthState(
    val user: User,
    val error: AuthError?,
    val loading: Boolean,
)
