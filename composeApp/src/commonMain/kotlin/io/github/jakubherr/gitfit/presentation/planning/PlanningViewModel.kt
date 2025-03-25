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
            is PlanAction.AddExercise -> addExercise(action.workout, action.exercise)
            is PlanAction.RemoveExercise -> TODO()
            is PlanAction.AddSet -> TODO()
        }
    }

    private fun addWorkout(workout: WorkoutPlan) {
        plan = plan.copy(workouts = plan.workouts + workout)
    }

    private fun saveWorkout() {
        val workouts = plan.workouts.toMutableList()
    }

    private fun renamePlan(name: String) {
        plan = plan.copy(name = name)
    }

    private fun addExercise(workout: WorkoutPlan, exercise: Exercise) {
        val workouts = plan.workouts.toMutableList()
        workouts[workout.idx] = workout.copy(blocks = workout.blocks + Block("", workout.blocks.size, exercise))
        plan = plan.copy(workouts = workouts)
    }

    private fun addSet(workout: WorkoutPlan, block: Block) {
        val workout = plan.workouts[workout.idx]
        val block = workout.blocks[block.idx]

        // val newBlock = block.copy(series = block.series + Series())
    }
}

sealed interface PlanAction {
    object SavePlan : PlanAction
    class RenamePlan(val name: String) : PlanAction
    class AddWorkout(val workout: WorkoutPlan) : PlanAction
    class SaveWorkout(val workout: WorkoutPlan) : PlanAction
    class AddExercise(val workout: WorkoutPlan, val exercise: Exercise) : PlanAction
    class RemoveExercise(val workout: WorkoutPlan, val index: Int) : PlanAction
    class AddSet(val workout: WorkoutPlan, val block: Block) : PlanAction
}
