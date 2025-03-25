package io.github.jakubherr.gitfit.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val description: String?,
    val primaryMuscle: List<MuscleGroup> = emptyList(),
    val secondaryMuscle: List<MuscleGroup> = emptyList(),
)
// some strength standards for each exercise?

enum class MuscleGroup {
    ARMS,
    LEGS,
    SHOULDERS,
    BACK,
    ABS,
    CHEST,
    FOREARMS,
}

val mockExercise =
    Exercise(
        "mock",
        "Bench press",
        "",
        listOf(MuscleGroup.CHEST),
        listOf(MuscleGroup.SHOULDERS),
    )
