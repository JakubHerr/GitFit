package io.github.jakubherr.gitfit.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val description: String?,
    val isCustom: Boolean = false,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscle: List<MuscleGroup> = emptyList(),
)

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
        false,
        MuscleGroup.CHEST,
        listOf(MuscleGroup.SHOULDERS),
    )
