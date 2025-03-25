package io.github.jakubherr.gitfit.presentation.planning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan

class PlanningViewModel: ViewModel() {
    var plan by mutableStateOf(Plan("","","",""))
        private set

    fun onAction(action: PlanAction) {
        when (action) {
            is PlanAction.SavePlan -> TODO()
            is PlanAction.AddWorkout -> addWorkout(action.workout)
            is PlanAction.RenamePlan -> renamePlan(action.name)
            is PlanAction.SaveWorkout -> TODO()
            is PlanAction.AddExercise -> addBlock(action.workout, action.exercise)
            is PlanAction.RemoveExercise -> removeBlock(action.workout, action.block)
            is PlanAction.AddSet -> addSet(action.workout, action.block)
        }
    }

    private fun renamePlan(name: String) {
        plan = plan.copy(name = name)
    }

    private fun savePlan() {

    }

    private fun addWorkout(workout: WorkoutPlan) {
        plan = plan.copy(workouts = plan.workouts + workout)
    }

    private fun updateWorkout(workout: WorkoutPlan) {
        val workouts = plan.workouts.toMutableList()
        workouts[workout.idx] = workout
        plan = plan.copy(workouts = workouts)
    }

    private fun addBlock(workout: WorkoutPlan, exercise: Exercise) {
        val updated = workout.copy(blocks = workout.blocks + Block("", workout.blocks.size, exercise))
        updateWorkout(updated)
    }

    private fun updateBlock(workout: WorkoutPlan, block: Block) {
        val newBlocks = workout.blocks.toMutableList()
        newBlocks[block.idx] = block
        updateWorkout(workout.copy(blocks = newBlocks))
    }

    private fun removeBlock(workout: WorkoutPlan, block: Block) {
        val newWorkout = workout.copy(blocks = workout.blocks - block)
        updateWorkout(newWorkout)
    }

    private fun addSet(workout: WorkoutPlan, block: Block) {
        val newBlock = block.copy(series = block.series + Series("", block.series.size, null, null, false))
        updateBlock(workout, newBlock)
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
    class AddWorkout(val workout: WorkoutPlan) : PlanAction
    class SaveWorkout(val workout: WorkoutPlan) : PlanAction
    class AddExercise(val workout: WorkoutPlan, val exercise: Exercise) : PlanAction
    class RemoveExercise(val workout: WorkoutPlan, val block: Block) : PlanAction
    class AddSet(val workout: WorkoutPlan, val block: Block) : PlanAction
}
