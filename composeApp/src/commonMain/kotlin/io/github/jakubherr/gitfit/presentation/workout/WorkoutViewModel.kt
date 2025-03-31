package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.ProgressionType
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
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

    var error by mutableStateOf<Workout.Error?>(null)
        private set

    fun onAction(action: WorkoutAction) {
        when (action) {
            is WorkoutAction.StartNewWorkout -> startNewWorkout()
            is WorkoutAction.StartPlannedWorkout -> startPlannedWorkout(action.planId, action.workoutIdx)
            is WorkoutAction.CompleteCurrentWorkout -> completeCurrentWorkout(action.workoutId)
            is WorkoutAction.DeleteWorkout -> deleteWorkout(action.workoutId)
            is WorkoutAction.AskForExercise -> { }
            is WorkoutAction.AddBlock -> addBlock(action.workoutId, action.exercise)
            is WorkoutAction.AddSet -> addSet(action.workoutId, action.blockIdx, action.set)
            is WorkoutAction.ModifySeries -> modifySeries(action.blockIdx, action.set)
            is WorkoutAction.ErrorHandled -> error = null
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

    private fun completeCurrentWorkout(workoutId: String) {
        viewModelScope.launch {
            if (error == null) workoutRepository.completeWorkout(workoutId)
            handleProgression(workoutId)
        }
    }

    private suspend fun handleProgression(workoutId: String) {
        val workout = workoutRepository.getWorkout(workoutId)

        println("DBG: progressing workout: ${workout?.id}")

        workout?.let {
            val isFromPlan = workout.planId != null && workout.planWorkoutIdx != null
            if (!isFromPlan) return

            val blocksWithProgression = workout.blocks.filter { it.progressionSettings != null }.ifEmpty { return }
            println("DBG: workout has ${blocksWithProgression.size} blocks with progression")

            blocksWithProgression.forEach { block ->
                val settings = block.progressionSettings!!
                val minimumReps = settings.repThreshold

                val shouldProgress = block.series.all { series ->
                    series.completed && series.weight!! >= settings.weightThreshold && series.repetitions!! >= settings.repThreshold
                }

                println("DBG: block with valid progression detected")

                if (shouldProgress) {
                    when (settings.type) {
                        ProgressionType.INCREASE_WEIGHT-> { /* TODO increase weight of all series + weight threshold */ }
                        ProgressionType.INCREASE_REPS -> { /* TODO increase reps of all series + rep threshold*/ }
                    }
                }
            }

            // TODO modify WorkoutPlan based on updated values
        }
    }

    private fun deleteWorkout(workoutId: String) {
        viewModelScope.launch { workoutRepository.deleteWorkout(workoutId) }
    }

    private fun addBlock(
        workoutId: String,
        exercise: Exercise,
    ) {
        viewModelScope.launch { workoutRepository.addBlock(workoutId, exercise) }
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

    class AddBlock(val workoutId: String, val exercise: Exercise) : WorkoutAction

    class AddSet(val workoutId: String, val blockIdx: Int, val set: Series) : WorkoutAction

    class ModifySeries(val blockIdx: Int, val set: Series) : WorkoutAction

    class CompleteCurrentWorkout(val workoutId: String) : WorkoutAction

    class DeleteWorkout(val workoutId: String) : WorkoutAction

    class AskForExercise(val workoutId: String) : WorkoutAction

    object ErrorHandled : WorkoutAction
}
