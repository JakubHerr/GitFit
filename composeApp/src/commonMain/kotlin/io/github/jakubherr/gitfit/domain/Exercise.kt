package io.github.jakubherr.gitfit.domain


data class Exercise(
    val id: Long,
    val name: String,
    val description: String,
    val primaryMuscle: List<MuscleGroup>,
    val secondaryMuscle: List<MuscleGroup>,
)

enum class MuscleGroup {
    ARMS,
    LEGS,
    SHOULDERS,
    BACK,
    ABS,
    CHEST,
    FOREARMS
}

val mockExercise = Exercise(
    -1,
    "Bench press",
    "",
    listOf(MuscleGroup.CHEST),
    listOf(MuscleGroup.SHOULDERS)
)
