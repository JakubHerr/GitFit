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
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val planRepository: PlanRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    // TODO: how to detect a workout modification? If device is offline, the launched coroutine will not finish
    //  maybe make all repository actions return result?
    private val currentUser = authRepository.currentUserFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    var currentWorkout = currentUser.flatMapLatest { user ->
        workoutRepository.observeCurrentWorkoutOrNull(user?.id ?: "")
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000L),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val plannedWorkouts = currentUser.flatMapLatest { user ->
        workoutRepository.getPlannedWorkouts(user?.id ?: "")
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5_000L),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val completedWorkouts = currentUser.flatMapLatest { user ->
        workoutRepository.getCompletedWorkouts(user?.id ?: "")
    }.stateIn(
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
            viewModelScope.launch {
                handleProgression(workout)
                progressionHandled = true
            }
            viewModelScope.launch {
                while (currentWorkout.value != null || !progressionHandled) delay(1000)
                workoutSaved = true
            }
        }
    }


    private suspend fun handleProgression(workout: Workout) {
        workout.let {
            // if not part of plan, exit
            val isFromPlan = workout.planId != null && workout.planWorkoutIdx != null
            if (!isFromPlan) return

            // fetch plan that workout record was based on and its workout plan
            val plan = planRepository.getCustomPlan(authRepository.currentUser.id, workout.planId!!) ?: return
            var workoutPlan = plan.workoutPlans.getOrNull(workout.planWorkoutIdx!!) ?: return
            workoutPlan = workoutPlan.progressPlan(workout)

            // update plan in database
            progressionHandled = true
            planRepository.saveCustomPlan(authRepository.currentUser.id, plan.updateWorkoutPlan(workoutPlan))
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
