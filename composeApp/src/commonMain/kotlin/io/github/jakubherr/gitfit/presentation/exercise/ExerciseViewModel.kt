package io.github.jakubherr.gitfit.presentation.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.data.repository.FirebaseAuthRepository
import io.github.jakubherr.gitfit.domain.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val authRepository: FirebaseAuthRepository,
) : ViewModel() {
    val flow = exerciseRepository.getDefaultExercises()

    fun onAction(action: ExerciseAction) {
        when (action) {
            is ExerciseAction.ExerciseCreated -> createExercise(action.exercise)
            else -> { }
        }
    }

    private fun createExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.addCustomExercise(authRepository.currentUser.id, exercise)
        }
    }

    private fun getExerciseHistory(exercise: Exercise) {
        // TODO
    }
}

sealed interface ExerciseAction {
    class ExerciseSelected(val exercise: Exercise) : ExerciseAction

    object CreateExerciseSelected : ExerciseAction

    class ExerciseCreated(val exercise: Exercise) : ExerciseAction
}
