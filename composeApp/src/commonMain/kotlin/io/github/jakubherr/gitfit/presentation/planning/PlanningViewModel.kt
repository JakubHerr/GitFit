package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jakubherr.gitfit.domain.AuthRepository
import io.github.jakubherr.gitfit.domain.PlanRepository
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.ProgressionSettings
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class PlanningViewModel(
    private val planRepository: PlanRepository,
    private val authRepository: AuthRepository,
): ViewModel() {
    var plan: Plan by mutableStateOf(Plan.Empty)
    var error: Plan.Error? by mutableStateOf(null)

    val userPlans get() =
        if (authRepository.currentUser.loggedIn) planRepository.getCustomPlans(authRepository.currentUser.id)
        else emptyFlow()

    val predefinedPlans = planRepository.getPredefinedPlans()

    fun onAction(action: PlanAction) {
        when (action) {
            is PlanAction.SavePlan -> savePlan()
            is PlanAction.RenamePlan -> plan = plan.copy(name = action.name)
            is PlanAction.EditPlan -> plan = action.plan
            is PlanAction.DiscardPlan -> discardPlan()
            is PlanAction.DeletePlan -> deletePlan(action.planId)

            is PlanAction.AddWorkout -> addWorkout(action.workout)
            is PlanAction.SaveWorkout -> saveWorkout(action.workout)
            is PlanAction.DeleteWorkout -> removeWorkout(action.workout)

            is PlanAction.AddExercise -> addExercise(action.workoutIdx, action.exercise)
            is PlanAction.RemoveExercise -> removeBlock(action.workout, action.block)

            is PlanAction.AddSet -> addSet(action.workout, action.block)
            is PlanAction.EditSet -> updateSet(action.workout, action.block, action.set)
            is PlanAction.RemoveSet -> removeSet(action.workout, action.block, action.set)

            PlanAction.ErrorHandled -> error = null
        }
    }

    private fun savePlan() {
        val user = authRepository.currentUser
        if (!user.loggedIn) return

        error = plan.error
        if (error != null) return

        viewModelScope.launch {
            planRepository.saveCustomPlan(user.id, plan)
            plan = Plan.Empty
        }
    }

    private fun discardPlan() {
        plan = Plan.Empty
    }

    private fun deletePlan(planId: String) {
        val user = authRepository.currentUser
        if (!user.loggedIn) return

        viewModelScope.launch { planRepository.deleteCustomPlan(user.id, planId) }
    }

    private fun addWorkout(workout: WorkoutPlan) {
        plan = plan.copy(workouts = plan.workouts + workout)
    }

    private fun updateWorkout(workout: WorkoutPlan) {
        val workouts = plan.workouts.toMutableList()
        workouts[workout.idx] = workout
        plan = plan.copy(workouts = workouts)
    }

    private fun removeWorkout(workout: WorkoutPlan) {
        val newWorkout = plan.workouts - workout
        plan = plan.copy(workouts = newWorkout)
    }

    private fun saveWorkout(workout: WorkoutPlan) {
        val workoutError = workout.toWorkout().error ?: return
        error = Plan.Error.InvalidWorkout(workoutError)
    }

    private fun addExercise(workoutIdx: Int, exercise: Exercise) {
        val workout = plan.workouts[workoutIdx]
        val updated = workout.copy(blocks = workout.blocks + Block(workout.blocks.size, exercise))
        updateWorkout(updated)
    }

    private fun updateBlock(workout: WorkoutPlan, block: Block) {
        val newBlocks = workout.blocks.toMutableList()
        newBlocks[block.idx] = block
        updateWorkout(workout.copy(blocks = newBlocks))
    }

    private fun setProgression(workout: WorkoutPlan, block: Block, progression: ProgressionSettings) {
        // TODO implement progression saving and evaluation
    }

    private fun removeBlock(workout: WorkoutPlan, block: Block) {
        val newWorkout = workout.copy(blocks = workout.blocks - block)
        updateWorkout(newWorkout)
    }

    private fun addSet(workout: WorkoutPlan, block: Block) {
        val newBlock = block.copy(series = block.series + Series(block.series.size, null, null, false))
        updateBlock(workout, newBlock)
    }

    private fun removeSet(workout: WorkoutPlan, block: Block, set: Series) {
        updateBlock(workout, block.copy(series = block.series - set))
    }

    private fun updateSet(workout: WorkoutPlan, block: Block, set: Series) {
        val newSeries = plan.workouts[workout.idx].blocks[block.idx].series.toMutableList()
        newSeries[set.idx] = set
        updateBlock(workout, block.copy(series = newSeries))
    }
}

sealed interface PlanAction {
    object SavePlan : PlanAction
    class RenamePlan(val name: String) : PlanAction
    class EditPlan(val plan: Plan) : PlanAction
    object DiscardPlan : PlanAction
    class DeletePlan(val planId: String) : PlanAction

    class AddWorkout(val workout: WorkoutPlan) : PlanAction
    class SaveWorkout(val workout: WorkoutPlan) : PlanAction
    class DeleteWorkout(val workout: WorkoutPlan) : PlanAction

    class AddExercise(val workoutIdx: Int, val exercise: Exercise) : PlanAction
    class RemoveExercise(val workout: WorkoutPlan, val block: Block) : PlanAction

    class AddSet(val workout: WorkoutPlan, val block: Block) : PlanAction
    class EditSet(val workout: WorkoutPlan, val block: Block, val set: Series) : PlanAction
    class RemoveSet(val workout: WorkoutPlan, val block: Block, val set: Series) : PlanAction

    object ErrorHandled : PlanAction
}


