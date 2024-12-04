package io.github.jakubherr.gitfit.presentation.exercise

import androidx.lifecycle.ViewModel
import io.github.jakubherr.gitfit.domain.Exercise
import io.github.jakubherr.gitfit.domain.ExerciseRepository

class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {
    val flow = repository.getAllExercises()

    fun onAction(action: ExerciseAction) {
        when(action) {
            is ExerciseAction.ExerciseCreated -> createExercise(action.exercise)
            else -> { }
        }
    }

    private fun createExercise(exercise: Exercise) {
        repository.createExercise(exercise)
    }

    private fun getExerciseHistory(exercise: Exercise) {
        // TODO
    }
}

sealed interface ExerciseAction {
    class ExerciseSelected(val id: Long) : ExerciseAction
    object CreateExerciseSelected : ExerciseAction
    class ExerciseCreated(val exercise: Exercise) : ExerciseAction
}