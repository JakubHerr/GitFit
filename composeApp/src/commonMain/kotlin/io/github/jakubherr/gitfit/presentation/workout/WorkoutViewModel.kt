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
import io.github.jakubherr.gitfit.domain.model.ProgressionType
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.presentation.shared.Resource
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

    var fetchedWorkout by mutableStateOf(Resource.Loading as Resource<Workout>)
        private set

    var workoutSaved by mutableStateOf(false)
        private set

    var error by mutableStateOf<Workout.Error?>(null)
        private set

    fun onAction(action: WorkoutAction) {
        when (action) {
            is WorkoutAction.StartNewWorkout -> startNewWorkout()
            is WorkoutAction.StartPlannedWorkout -> startPlannedWorkout(action.planId, action.workoutIdx)
            is WorkoutAction.CompleteCurrentWorkout -> completeCurrentWorkout()
            is WorkoutAction.DeleteWorkout -> deleteWorkout(action.workoutId)
            is WorkoutAction.FetchWorkout -> fetchWorkout(action.workoutId)
            is WorkoutAction.AskForExercise -> { }
            is WorkoutAction.AddBlock -> addBlock(action.workoutId, action.exercise)
            is WorkoutAction.RemoveBlock -> removeBlock(action.workoutId, action.block)
            is WorkoutAction.AddSet -> addSeries(action.workoutId, action.blockIdx, action.series)
            is WorkoutAction.ModifySeries -> modifySeries(action.blockIdx, action.series)
            is WorkoutAction.DeleteLastSeries -> deleteLastSeries(action.workoutId, action.blockIdx, action.series)
        }
    }

    private fun removeBlock(workoutId: String, block: Block) {
        viewModelScope.launch {
            workoutRepository.removeBlock(workoutId, block.idx)
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

    private fun completeCurrentWorkout() {
        val workout = currentWorkout.value ?: return

        if (workout.error == null) {
            // This is a hack to fix offline-first saving
            // if the device is offline, GitLive will suspend coroutine indefinitely until the record is synchronized
            // to check for success, it is necessary to observe completion indirectly through flow
            viewModelScope.launch { workoutRepository.completeWorkout(workout) }
            viewModelScope.launch { handleProgression(workout) }
            viewModelScope.launch {
                while (currentWorkout.value != null) delay(1000)
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
                        ProgressionType.INCREASE_WEIGHT-> {
                            println("DBG: progressing ${recordedBlock.exercise.name} by ${settings.weightThreshold}")

                            workoutPlanCopy = workoutPlanCopy.updateBlock(planBlock.progressWeight(settings.incrementWeightByKg))
                        }
                        ProgressionType.INCREASE_REPS -> {
                            println("DBG: progressing ${recordedBlock.exercise.name} by ${settings.incrementRepsBy}")

                            workoutPlanCopy = workoutPlanCopy.updateBlock(planBlock.progressReps(settings.incrementRepsBy))
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

    private fun fetchWorkout(workoutId: String) {
        fetchedWorkout = Resource.Loading
        viewModelScope.launch {
            workoutRepository.getWorkout(workoutId)
                .onSuccess { fetchedWorkout = Resource.Success(it) }
                .onFailure { fetchedWorkout = Resource.Failure(it) }
        }
    }

    private fun addBlock(
        workoutId: String,
        exercise: Exercise,
    ) {
        viewModelScope.launch {
            workoutRepository.addBlock(workoutId, exercise)
        }
    }

    private fun addSeries(
        workoutId: String,
        blockIdx: Int,
        series: Series,
    ) {
        viewModelScope.launch { workoutRepository.addSeries(workoutId, blockIdx, series) }
    }

    private fun modifySeries(
        blockIdx: Int,
        series: Series,
    ) {
        println("DBG: saving series $series to database")
        val workoutId = currentWorkout.value?.id ?: return // TODO error handling
        viewModelScope.launch { workoutRepository.modifySeries(workoutId, blockIdx, series) }
    }

    private fun deleteLastSeries(workoutId: String, blockIdx: Int, series: Series) {
        viewModelScope.launch {
            workoutRepository.removeSeries(workoutId, blockIdx, series)
        }
    }
}

sealed interface WorkoutAction {
    object StartNewWorkout : WorkoutAction
    class StartPlannedWorkout(val planId: String, val workoutIdx: Int) : WorkoutAction
    object CompleteCurrentWorkout : WorkoutAction
    class DeleteWorkout(val workoutId: String) : WorkoutAction
    class FetchWorkout(val workoutId: String): WorkoutAction

    class AddBlock(val workoutId: String, val exercise: Exercise) : WorkoutAction
    class RemoveBlock(val workoutId: String, val block: Block) : WorkoutAction

    class AddSet(val workoutId: String, val blockIdx: Int, val series: Series) : WorkoutAction
    class ModifySeries(val blockIdx: Int, val series: Series) : WorkoutAction
    class DeleteLastSeries(val workoutId: String, val blockIdx: Int, val series: Series) : WorkoutAction

    class AskForExercise(val workoutId: String) : WorkoutAction
}
