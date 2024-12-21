package io.github.jakubherr.gitfit.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.mockWorkout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    var currentWorkout = workoutRepository.observeCurrentWorkoutOrNull().stateIn(
        scope = viewModelScope,
        initialValue = mockWorkout,
        started = SharingStarted.Eagerly  // TODO improve
    ).also {
        println("DBG: currentWorkout flow initiated")
    }

    val cold = workoutRepository.observeCurrentWorkoutOrNull()

    fun onAction(action: WorkoutAction) {
        when(action) {
            is WorkoutAction.AddBlock -> addBlock(action.workoutId, action.exerciseId)
            is WorkoutAction.AddSet -> addSet(action.workoutId, action.blockId, action.set)
            is WorkoutAction.ToggleSetCompletion -> toggleSetCompletion(action.setId)
            is WorkoutAction.AskForExercise -> { }
            is WorkoutAction.DeleteWorkout -> deleteWorkout(action.workoutId)
            is WorkoutAction.CompleteWorkout -> completeWorkout(action.workoutId)
        }
    }

    // starts a new unplanned workout
    fun startWorkout() {
        if (currentWorkout.value == null) viewModelScope.launch { workoutRepository.startNewWorkout() }
    }

    // adds a new empty block with exercise to workout
    private fun addBlock(workoutId: String, exerciseId: String) {
        viewModelScope.launch { workoutRepository.addBlock(workoutId, exerciseId) }
    }

    private fun addSet(workoutId: String, blockId: String, set: Series) {
        viewModelScope.launch { workoutRepository.addSeries(workoutId, blockId, set) }
    }

    private fun toggleSetCompletion(setId: String) {
        viewModelScope.launch { workoutRepository.toggleSeries(setId) }
    }

    private fun completeWorkout(workoutId: String) {
        viewModelScope.launch { workoutRepository.completeWorkout(workoutId) }
    }

    private fun deleteWorkout(workoutId: String) {
        viewModelScope.launch { workoutRepository.deleteWorkout(workoutId) }
    }
}

sealed interface WorkoutAction {
    class AddBlock(val workoutId: String, val exerciseId: String) : WorkoutAction
    class AddSet(val workoutId: String, val blockId: String, val set: Series) : WorkoutAction
    class ToggleSetCompletion(val setId: String) : WorkoutAction
    class CompleteWorkout(val workoutId: String) : WorkoutAction
    class DeleteWorkout(val workoutId: String) : WorkoutAction
    object AskForExercise : WorkoutAction
}