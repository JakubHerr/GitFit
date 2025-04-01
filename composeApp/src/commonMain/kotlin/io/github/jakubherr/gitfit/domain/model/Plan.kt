package io.github.jakubherr.gitfit.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val id: String,
    val userId: String?,
    val name: String,
    val description: String,
    val workoutPlans: List<WorkoutPlan> = emptyList()
) {
    companion object {
        val Empty = Plan("","","","")
    }

    val error: Error? get() {
        val invalidWorkout = workoutPlans.map { it.toWorkout() }.find { it.error != null }

        return when {
            name.isBlank() -> Error.InvalidPlanName
            workoutPlans.isEmpty() -> Error.NoWorkoutInPlan
            invalidWorkout != null -> Error.InvalidWorkout(invalidWorkout.error!!)
            else -> null
        }
    }

    fun addWorkoutPlan(workoutPlan: WorkoutPlan): Plan = copy(workoutPlans = workoutPlans + workoutPlan)

    fun updateWorkoutPlan(workoutPlan: WorkoutPlan): Plan {
        val workouts = workoutPlans.toMutableList()
        workouts[workoutPlan.idx] = workoutPlan
        return copy(workoutPlans = workouts)
    }

    fun removeWorkoutPlan(workoutPlan: WorkoutPlan): Plan = copy(workoutPlans = workoutPlans - workoutPlan)

    fun addExercise(workoutPlanIdx: Int, exercise: Exercise): Plan {
        val workout = workoutPlans[workoutPlanIdx]
        return updateWorkoutPlan(workout.addBlock(exercise))
    }

    fun removeBlock(workoutPlan: WorkoutPlan, block: Block): Plan = updateWorkoutPlan(workoutPlan.removeBlock(block))

    fun addSeries(workoutPlan: WorkoutPlan, block: Block) : Plan {
        return updateWorkoutPlan(workoutPlan.updateBlock(block.addSeries()))
    }

    fun updateSeries(workoutPlan: WorkoutPlan, block: Block, series: Series) : Plan {
        return updateWorkoutPlan(workoutPlan.updateBlock(block.updateSeries(series)))
    }

    fun removeSeries(workoutPlan: WorkoutPlan, block: Block, series: Series) : Plan {
        return updateWorkoutPlan(workoutPlan.updateBlock(block.removeSeries(series)))
    }

    fun setProgression(workoutPlan: WorkoutPlan, block: Block, progressionSettings: ProgressionSettings?): Plan {
        return updateWorkoutPlan(workoutPlan.updateBlock(block.copy(progressionSettings = progressionSettings)))
    }

    sealed class Error(val message: String) {
        object InvalidPlanName: Error("Plan name can not be blank")
        object NoWorkoutInPlan: Error("Plan has no workout days")
        class InvalidWorkout(val error: Workout.Error): Error(error.message)
    }
}
