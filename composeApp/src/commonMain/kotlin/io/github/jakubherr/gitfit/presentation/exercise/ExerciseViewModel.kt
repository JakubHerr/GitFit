package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.repository.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.presentation.shared.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val defaultExercises = exerciseRepository.getDefaultExercises()
    val customExercises = exerciseRepository.getCustomExercises(authRepository.currentUser.id)

    var fetchedExercise by mutableStateOf<Resource<Exercise>>(Resource.Loading)
        private set

    var selectedExercise by mutableStateOf<Exercise?>(null)
        private set

    fun onAction(action: ExerciseAction) {
        when (action) {
            is ExerciseAction.CreateExercise -> createExercise(action.exercise)
            is ExerciseAction.EditCustomExercise -> editExercise(action.exercise)
            is ExerciseAction.DeleteCustomExercise -> deleteCustomExercise(action.exerciseId)
            // is ExerciseAction.FetchExercise -> fetchExercise(action.exerciseId, action.isCustom)
            is ExerciseAction.SelectExercise -> selectedExercise = action.exercise
        }
    }

    private fun createExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.addCustomExercise(authRepository.currentUser.id, exercise)
        }
    }

    private fun editExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.editCustomExercise(authRepository.currentUser.id, exercise)
        }
    }

    private fun deleteCustomExercise(exerciseId: String) {
        viewModelScope.launch {
            exerciseRepository.removeCustomExercise(authRepository.currentUser.id, exerciseId)
        }
    }

    // TODO: AVOID fetching exercise if it is not necessary, it can take a long time in offline mode
    private fun fetchExercise(exerciseId: String, isCustom: Boolean) {
        fetchedExercise = Resource.Loading
        viewModelScope.launch {
            if (isCustom) {
                exerciseRepository.getCustomExercise(authRepository.currentUser.id, exerciseId)
                    .onSuccess { fetchedExercise = Resource.Success(it) }
                    .onFailure { Resource.Failure(it) }
            } else {
                exerciseRepository.getDefaultExercise(exerciseId)
                    .onSuccess { fetchedExercise = Resource.Success(it) }
                    .onFailure { fetchedExercise = Resource.Failure(it) }
            }
        }
    }
}

sealed interface ExerciseAction {
    class CreateExercise(val exercise: Exercise) : ExerciseAction
    class EditCustomExercise(val exercise: Exercise) : ExerciseAction
    class DeleteCustomExercise(val exerciseId: String) : ExerciseAction

    // class FetchExercise(val exerciseId: String, val isCustom: Boolean) : ExerciseAction
    class SelectExercise(val exercise: Exercise) : ExerciseAction
}
