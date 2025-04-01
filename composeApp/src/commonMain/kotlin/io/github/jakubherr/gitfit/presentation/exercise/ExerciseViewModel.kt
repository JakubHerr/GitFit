package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.data.repository.FirebaseAuthRepository
import io.github.jakubherr.gitfit.domain.repository.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val authRepository: FirebaseAuthRepository,
) : ViewModel() {
    val defaultExercises = exerciseRepository.getDefaultExercises()
    val customExercises = exerciseRepository.getCustomExercises(authRepository.currentUser.id)

    // TODO add loading/error/result state
    var lastFetchedExercise by mutableStateOf<Exercise?>(null)
        private set

    fun onAction(action: ExerciseAction) {
        when (action) {
            is ExerciseAction.ExerciseCreated -> createExercise(action.exercise)
            is ExerciseAction.FetchExercise -> fetchExercise(action.exerciseId)
            else -> { }
        }
    }

    private fun createExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.addCustomExercise(authRepository.currentUser.id, exercise)
        }
    }

    private fun fetchExercise(exerciseId: String) {
        viewModelScope.launch {
            exerciseRepository.getDefaultExerciseById(exerciseId)
                .onSuccess { lastFetchedExercise = it }
                .onFailure {
                    val custom = exerciseRepository.getCustomExerciseById(authRepository.currentUser.id, exerciseId)
                    custom.onSuccess { lastFetchedExercise = it }
                }
        }
    }
}

sealed interface ExerciseAction {
    // TODO delete custom exercise
    // TODO edit custom exercise
    class ExerciseSelected(val exercise: Exercise) : ExerciseAction
    object CreateExerciseSelected : ExerciseAction
    class ExerciseCreated(val exercise: Exercise) : ExerciseAction
    class FetchExercise(val exerciseId: String) : ExerciseAction
}
