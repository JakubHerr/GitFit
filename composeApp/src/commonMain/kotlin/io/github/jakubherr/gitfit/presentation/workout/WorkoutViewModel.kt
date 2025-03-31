package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.domain.repository.PlanRepository
import io.github.jakubherr.gitfit.domain.repository.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.ProgressionType
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val planRepository: PlanRepository,
    private val authRepository: AuthRepository,
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

    var workoutSaved by mutableStateOf(false)
        private set

    var error by mutableStateOf<Workout.Error?>(null)
        private set

    fun onAction(action: WorkoutAction) {
        when (action) {
            is WorkoutAction.StartNewWorkout -> startNewWorkout()
            // TODO prevent user from starting a planned workout if one workout is already in progress!
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
            if (error == null) {
                handleProgression(workoutId)
                workoutRepository.completeWorkout(workoutId)
                workoutSaved = true
            }
        }
    }

    private suspend fun handleProgression(workoutId: String) {
        // get workout record that was finished
        val workout = workoutRepository.getWorkout(workoutId)
        if (workout == null) {
            println("DBG: failed to fetch workout $workoutId!")
            return
        }

        println("DBG: progressing workout: ${workout.id}")
        workout.let {
            // if not part of plan, exit
            val isFromPlan = workout.planId != null && workout.planWorkoutIdx != null
            if (!isFromPlan) return

            // fetch plan that workout record was based on and its workout plan
            val plan = planRepository.getCustomPlan(authRepository.currentUser.id, workout.planId!!) ?: return
            var workoutPlanCopy = plan.workoutPlans.getOrNull(workout.planWorkoutIdx!!) ?: return

            // TODO how to detect a plan that is different from workout plan?
            //  progression can not be changed mid-workout -> no workouts with progression should be missing from plan
            //  removing a block with progression will just prevent it from progressing in the plan -> OK
            //  user can add a new block without a progression -> OK
            //  user can not change order of exercises -> indexing should be OK

            // filter out all blocks in workout record that have progression, if none are found, exit
            val blocksWithProgression = workout.blocks.filter { it.progressionSettings != null }.ifEmpty { return }
            println("DBG: workout has ${blocksWithProgression.size} blocks with progression")

            // check every recorded block with progression for progress threshold criteria
            blocksWithProgression.forEach { block ->
                val settings = block.progressionSettings!!
                val shouldProgress = block.series.all { series ->
                    series.completed && series.weight!! >= settings.weightThreshold && series.repetitions!! >= settings.repThreshold
                }

                // if criteria was met
                if (shouldProgress) {
                    println("DBG: block with valid progression detected")
                    // increment all block weight/reps by increment
                    // increment value in progression setting
                    // save block to workout plan and then save it to plan
                    when (settings.type) {
                        ProgressionType.INCREASE_WEIGHT-> {
                            println("DBG: progressing ${block.exercise.name} by ${settings.weightThreshold}")
                            val newBlock = block.progressWeight(settings.incrementWeightByKg)
                            workoutPlanCopy = workoutPlanCopy.updateBlock(newBlock)
                        }
                        ProgressionType.INCREASE_REPS -> {
                            println("DBG: progressing ${block.exercise.name} by ${settings.incrementRepsBy}")

                            workoutPlanCopy = workoutPlanCopy.updateBlock(block.progressReps(settings.incrementRepsBy))
                        }
                    }
                }
            }

            // update plan in database
            println("DBG: Saving updated workout plan")
            planRepository.saveCustomPlan(authRepository.currentUser.id, plan.updateWorkoutPlan(workoutPlanCopy))
            println("DBG: plan saved")
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
    class CompleteCurrentWorkout(val workoutId: String) : WorkoutAction
    class DeleteWorkout(val workoutId: String) : WorkoutAction

    class AddBlock(val workoutId: String, val exercise: Exercise) : WorkoutAction
    // TODO Remove block

    class AddSet(val workoutId: String, val blockIdx: Int, val set: Series) : WorkoutAction
    class ModifySeries(val blockIdx: Int, val set: Series) : WorkoutAction
    // TODO remove series

    class AskForExercise(val workoutId: String) : WorkoutAction
    object ErrorHandled : WorkoutAction
}
