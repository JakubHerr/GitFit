package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.domain.repository.PlanRepository
import io.github.jakubherr.gitfit.domain.repository.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.ProgressionType
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val planRepository: PlanRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    // TODO: how to detect a workout modification? If device is offline, the launched coroutine will not finish
    //  maybe make all repository actions return result?
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

    val completedWorkouts =
        workoutRepository.getCompletedWorkouts().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000L),
        )

    var selectedWorkout by mutableStateOf<Workout?>(null)

    var workoutSaved by mutableStateOf(false)
        private set

    private var progressionHandled = false

    var error by mutableStateOf<Workout.Error?>(null)
        private set

    fun onAction(action: WorkoutAction) {
        when (action) {
            is WorkoutAction.StartNewWorkout -> startNewWorkout()
            is WorkoutAction.StartPlannedWorkout -> startPlannedWorkout(action.plan, action.workoutIdx)
            is WorkoutAction.CompleteCurrentWorkout -> completeCurrentWorkout()
            is WorkoutAction.DeleteWorkout -> deleteWorkout(action.workoutId)
            is WorkoutAction.SelectWorkout -> selectedWorkout = action.workout
            is WorkoutAction.AskForExercise -> {}
            is WorkoutAction.AddBlock -> addBlock(action.workout, action.exercise)
            is WorkoutAction.RemoveBlock -> removeBlock(action.workout, action.block)
            is WorkoutAction.AddSet -> addSeries(action.workout, action.blockIdx)
            is WorkoutAction.ModifySeries -> modifySeries(action.workout, action.blockIdx, action.series)
            is WorkoutAction.DeleteLastSeries -> deleteLastSeries(action.workout, action.blockIdx, action.series)
            WorkoutAction.NotifyWorkoutSaved -> {
                workoutSaved = false
                progressionHandled = false
            }
        }
    }

    private fun removeBlock(workout: Workout, block: Block) {
        viewModelScope.launch {
            workoutRepository.removeBlock(workout, block.idx)
        }
    }

    private fun startNewWorkout() {
        if (currentWorkout.value == null) {
            viewModelScope.launch {
                workoutRepository.startNewWorkout()
            }
        }
    }

    private fun startPlannedWorkout(plan: Plan, workoutIdx: Int) {
        if (currentWorkout.value == null) {
            viewModelScope.launch {
                workoutRepository.startWorkoutFromPlan(plan, workoutIdx)
            }
        }
    }

    private fun completeCurrentWorkout() {
        println("DBG: completing current workout ${currentWorkout.value}")
        val workout = currentWorkout.value ?: return

        if (workout.error == null) {
            // This is a hack to fix offline-first saving
            // if the device is offline, GitLive will suspend coroutine indefinitely until the record is synchronized
            // to check for success, it is necessary to observe completion indirectly through flow
            viewModelScope.launch { workoutRepository.completeWorkout(workout) }
            viewModelScope.launch { handleProgression(workout) }
            viewModelScope.launch {
                while (currentWorkout.value != null || !progressionHandled) delay(1000)
                workoutSaved = true
            }
        }
    }

    // Some restrictions were made on editing workout records with progression to prevent user from shooting themselves in the foot
    //  progression can not be changed mid-workout
    //  user can not remove block with progression
    //  user can add a new block without a progression -> OK
    //  user can not change order of exercises
    private suspend fun handleProgression(workout: Workout) {
        println("DBG: progressing workout: ${workout.id}")
        workout.let {
            // if not part of plan, exit
            val isFromPlan = workout.planId != null && workout.planWorkoutIdx != null
            println("DBG: workout is from plan: $isFromPlan")
            if (!isFromPlan) return

            // fetch plan that workout record was based on and its workout plan
            val plan = planRepository.getCustomPlan(authRepository.currentUser.id, workout.planId!!) ?: return
            var workoutPlanCopy = plan.workoutPlans.getOrNull(workout.planWorkoutIdx!!) ?: return

            // filter out all blocks in workout record that have progression. if none are found, exit
            val blocksWithProgression = workout.blocks.filter { it.progressionSettings != null }.ifEmpty { return }
            println("DBG: workout has ${blocksWithProgression.size} blocks with progression")

            // check every recorded block with progression for progress threshold criteria
            blocksWithProgression.forEach { recordedBlock ->
                val settings = recordedBlock.progressionSettings!!
                val shouldProgress = recordedBlock.series.all { series ->
                    series.completed && series.weight!! >= settings.weightThreshold && series.repetitions!! >= settings.repThreshold
                }

                // if criteria was met
                if (shouldProgress) {
                    println("DBG: block with valid progression detected")
                    val planBlock = workoutPlanCopy.blocks[recordedBlock.idx]

                    // increment all block weight/reps by increment
                    // increment value in progression setting
                    // save block to workout plan and then save it to plan
                    when (settings.type) {
                        ProgressionType.INCREASE_WEIGHT -> {
                            println("DBG: progressing ${recordedBlock.exercise.name} by ${settings.weightThreshold}")

                            workoutPlanCopy =
                                workoutPlanCopy.updateBlock(planBlock.progressWeight(settings.incrementWeightByKg))
                        }

                        ProgressionType.INCREASE_REPS -> {
                            println("DBG: progressing ${recordedBlock.exercise.name} by ${settings.incrementRepsBy}")

                            workoutPlanCopy =
                                workoutPlanCopy.updateBlock(planBlock.progressReps(settings.incrementRepsBy))
                        }
                    }
                }
            }

            // update plan in database
            println("DBG: Saving updated workout plan")
            progressionHandled = true
            planRepository.saveCustomPlan(authRepository.currentUser.id, plan.updateWorkoutPlan(workoutPlanCopy))
            println("DBG: plan saved")
        }
    }

    private fun deleteWorkout(workoutId: String) {
        viewModelScope.launch { workoutRepository.deleteWorkout(workoutId) }
    }

    private fun addBlock(
        workout: Workout,
        exercise: Exercise,
    ) {
        viewModelScope.launch {
            workoutRepository.addBlock(workout, exercise)
        }
    }

    private fun addSeries(
        workout: Workout,
        blockIdx: Int
    ) {
        viewModelScope.launch { workoutRepository.addSeries(workout, blockIdx) }
    }

    private fun modifySeries(
        workout: Workout,
        blockIdx: Int,
        series: Series,
    ) {
        viewModelScope.launch { workoutRepository.modifySeries(workout, blockIdx, series) }
    }

    private fun deleteLastSeries(
        workout: Workout,
        blockIdx: Int,
        series: Series
    ) {
        viewModelScope.launch {
            workoutRepository.removeSeries(workout, blockIdx, series)
        }
    }
}

sealed interface WorkoutAction {
    object StartNewWorkout : WorkoutAction
    class StartPlannedWorkout(val plan: Plan, val workoutIdx: Int) : WorkoutAction
    object CompleteCurrentWorkout : WorkoutAction
    class DeleteWorkout(val workoutId: String) : WorkoutAction
    class SelectWorkout(val workout: Workout) : WorkoutAction

    class AddBlock(val workout: Workout, val exercise: Exercise) : WorkoutAction
    class RemoveBlock(val workout: Workout, val block: Block) : WorkoutAction

    class AddSet(val workout: Workout, val blockIdx: Int) : WorkoutAction
    class ModifySeries(val workout: Workout, val blockIdx: Int, val series: Series) : WorkoutAction
    class DeleteLastSeries(val workout: Workout, val blockIdx: Int, val series: Series) : WorkoutAction

    class AskForExercise(val workoutId: String) : WorkoutAction
    object NotifyWorkoutSaved : WorkoutAction
}
