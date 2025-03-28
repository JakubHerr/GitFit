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
}
