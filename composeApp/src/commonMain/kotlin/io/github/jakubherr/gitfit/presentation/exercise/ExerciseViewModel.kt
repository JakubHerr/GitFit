package io.github.jakubherr.gitfit.presentation.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repository: ExerciseRepository,
) : ViewModel() {
    val flow = repository.getAllExercises()

    fun onAction(action: ExerciseAction) {
        when (action) {
            is ExerciseAction.ExerciseCreated -> createExercise(action.exercise)
            else -> { }
        }
    }

    private fun createExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.createExercise(exercise)
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
