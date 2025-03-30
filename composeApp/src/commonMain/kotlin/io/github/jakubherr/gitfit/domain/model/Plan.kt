package io.github.jakubherr.gitfit.domain.model

import kotlinx.serialization.Serializable

// a plan could be either predefined or custom made by the user
// custom plans are private
@Serializable
data class Plan(
    val id: String,
    val userId: String?,
    val name: String,
    val description: String,
    val workouts: List<WorkoutPlan> = emptyList()
    // difficulty
    // required equipment (maybe calculation based on present exercises would be nice)
    // category: upper/lower, PPL, full body
    // progression - increase reps per set, increase weight
) {
    companion object {
        val Empty = Plan("","","","")
    }

    val error: Error? get() {
        val invalidWorkout = workouts.map { it.toWorkout() }.find { it.error != null }

        return when {
            name.isBlank() -> Error.InvalidPlanName
            workouts.isEmpty() -> Error.NoWorkoutInPlan
            invalidWorkout != null -> Error.InvalidWorkout(invalidWorkout.error!!)
            else -> null
        }
    }

    sealed class Error(val message: String) {
        object InvalidPlanName: Error("Plan name can not be blank")
        object NoWorkoutInPlan: Error("Plan has no workout days")
        class InvalidWorkout(val error: Workout.Error): Error(error.message)
    }
}
