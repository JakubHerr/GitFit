package io.github.jakubherr.gitfit.presentation.exercise

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.repository.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val defaultExercises = exerciseRepository.getDefaultExercises()
    val customExercises = exerciseRepository.getCustomExercises(authRepository.currentUser.id)

    var fetchedExercise by mutableStateOf<ExerciseFetchResult>(ExerciseFetchResult.Loading)
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
        println("DBG: fetch exercise called")
        fetchedExercise = ExerciseFetchResult.Loading
        viewModelScope.launch {
            exerciseRepository.getDefaultExercise(exerciseId)
                .onSuccess { fetchedExercise = ExerciseFetchResult.Success(it) }
                .onFailure {
                    val custom = exerciseRepository.getCustomExercise(authRepository.currentUser.id, exerciseId)
                    custom
                        .onSuccess { fetchedExercise = ExerciseFetchResult.Success(it) }
                        .onFailure { ExerciseFetchResult.Failure(it) }
                }
        }
    }
}

sealed class ExerciseFetchResult {
    object Loading : ExerciseFetchResult()
    class Success(val exercise: Exercise) : ExerciseFetchResult()
    class Failure(val e: Throwable) : ExerciseFetchResult()
}

sealed interface ExerciseAction {
    // TODO delete custom exercise
    // TODO edit custom exercise
    class ExerciseSelected(val exercise: Exercise) : ExerciseAction
    object CreateExerciseSelected : ExerciseAction
    class ExerciseCreated(val exercise: Exercise) : ExerciseAction
    class FetchExercise(val exerciseId: String) : ExerciseAction
}
