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

    init {
        viewModelScope.launch {
            workoutRepository.debug()
        }
    }

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
            is WorkoutAction.AddSet -> addSet(action.blockId, action.set)
            is WorkoutAction.ToggleSetCompletion -> toggleSetCompletion(action.setId)
            WorkoutAction.AskForExercise -> { }
        }
    }

    // starts a new unplanned workout
    fun startWorkout() {
        if (currentWorkout.value == null) viewModelScope.launch { workoutRepository.startWorkout() }
    }

    // adds a new empty block with exercise to workout
    private fun addBlock(workoutId: Long, exerciseId: Long) {
        viewModelScope.launch { workoutRepository.addBlock(workoutId, exerciseId) }
    }

    private fun addSet(blockId: Long, set: Series) {
        viewModelScope.launch { workoutRepository.addEmptySeries(blockId) }
    }

    private fun toggleSetCompletion(setId: Long) {
        viewModelScope.launch { workoutRepository.toggleSeries(setId) }
    }

    private fun completeWorkout() {
        // TODO
    }
}

sealed interface WorkoutAction {
    class AddBlock(val workoutId: Long, val exerciseId: Long) : WorkoutAction
    class AddSet(val blockId: Long, val set: Series) : WorkoutAction
    class ToggleSetCompletion(val setId: Long) : WorkoutAction
    object AskForExercise : WorkoutAction
}