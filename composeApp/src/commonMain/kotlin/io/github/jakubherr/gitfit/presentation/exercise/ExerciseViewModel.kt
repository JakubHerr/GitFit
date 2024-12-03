package io.github.jakubherr.gitfit.presentation.exercise

import androidx.lifecycle.ViewModel

class ExerciseViewModel : ViewModel() {


    fun onAction(action: ExerciseAction) {

    }
}

sealed interface ExerciseAction {
    class ExerciseSelected(val id: Long) : ExerciseAction
    object AddExercise : ExerciseAction
}