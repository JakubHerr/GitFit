package io.github.jakubherr.gitfit.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Series
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {
    var currentWorkout =
        workoutRepository.observeCurrentWorkoutOrNull().stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed(5_000L),
        )

    val plannedWorkouts =
        workoutRepository.getPlannedWorkouts().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000L),
        )

    fun onAction(action: WorkoutAction) {
        when (action) {
            is WorkoutAction.StartNewWorkout -> startNewWorkout()
            is WorkoutAction.StartPlannedWorkout -> startPlannedWorkout(action.planId, action.workoutIdx)
            is WorkoutAction.CompleteWorkout -> completeWorkout(action.workoutId)
            is WorkoutAction.DeleteWorkout -> deleteWorkout(action.workoutId)
            is WorkoutAction.AskForExercise -> { }
            is WorkoutAction.AddBlock -> addBlock(action.workoutId, action.exerciseId)
            is WorkoutAction.AddSet -> addSet(action.workoutId, action.blockIdx, action.set)
            is WorkoutAction.ModifySeries -> modifySeries(action.blockIdx, action.set)
        }
    }

    private fun startNewWorkout() {
        if (currentWorkout.value == null) {
            viewModelScope.launch {
                workoutRepository.startNewWorkout()
            }
        }
    }

    private fun startPlannedWorkout(planId: String, workoutIdx: Int) {
        if (currentWorkout.value == null) {
            viewModelScope.launch {
                workoutRepository.startWorkoutFromPlan(planId, workoutIdx)
            }
        }
    }

    private fun completeWorkout(workoutId: String) {
        viewModelScope.launch { workoutRepository.completeWorkout(workoutId) }
    }

    private fun deleteWorkout(workoutId: String) {
        viewModelScope.launch { workoutRepository.deleteWorkout(workoutId) }
    }

    private fun addBlock(
        workoutId: String,
        exerciseId: String,
    ) {
        viewModelScope.launch { workoutRepository.addBlock(workoutId, exerciseId) }
    }

    private fun addSet(
        workoutId: String,
        blockIdx: Int,
        set: Series,
    ) {
        viewModelScope.launch { workoutRepository.addSeries(workoutId, blockIdx, set) }
    }

    private fun modifySeries(
        blockIdx: Int,
        set: Series,
    ) {
        val workoutId = currentWorkout.value?.id ?: return // TODO error handling
        viewModelScope.launch { workoutRepository.modifySeries(workoutId, blockIdx, set) }
    }
}

sealed interface WorkoutAction {
    object StartNewWorkout : WorkoutAction

    class StartPlannedWorkout(val planId: String, val workoutIdx: Int) : WorkoutAction

    class AddBlock(val workoutId: String, val exerciseId: String) : WorkoutAction

    class AddSet(val workoutId: String, val blockIdx: Int, val set: Series) : WorkoutAction

    class ModifySeries(val blockIdx: Int, val set: Series) : WorkoutAction

    class CompleteWorkout(val workoutId: String) : WorkoutAction

    class DeleteWorkout(val workoutId: String) : WorkoutAction

    object AskForExercise : WorkoutAction
}
