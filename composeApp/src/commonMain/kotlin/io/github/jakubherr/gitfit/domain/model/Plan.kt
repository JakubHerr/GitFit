package io.github.jakubherr.gitfit.domain.model

// a plan could be either predefined or custom made by the user
// custom plans are private
data class Plan(
    val id: String,
    val userId: String?,
    val name: String,
    val description: String,
    // difficulty
    // required equipment
    // category: upper/lower, PPL, full body
    // progression - increase reps per set, increase weight
)

