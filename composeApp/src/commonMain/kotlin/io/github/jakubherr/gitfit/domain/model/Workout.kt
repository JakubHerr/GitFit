package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: String,
    val userId: String,
    val name: String = "",
    val blocks: List<Block>,
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val completed: Boolean = false,
    val inProgress: Boolean = false,
)

@Serializable
data class WorkoutPlan(
    val name: String = "",
    val idx: Int,
    val blocks: List<Block>,
)

data class ProgressionSettings(
    val incrementWeightByKg: Double,
    val incrementReps: Int,
    val type: ProgressionType,
    val trigger: ProgressionTrigger,
    val threshold: Int,
)

enum class ProgressionType {
    INCREASE_REPS,
    INCREASE_WEIGHT,
}

enum class ProgressionTrigger {
    MINIMUM_REPS_EVERY_SET,
    EVERY_WORKOUT,
}

@Serializable
data class Block(
    val idx: Int,
    val exercise: Exercise,
    val series: List<Series> = emptyList(),
    val restTimeSeconds: Long? = null,
)

// this should be named Set but is named series because of name conflict with Set collection
@Serializable
data class Series(
    val idx: Int,
    val repetitions: Long?,
    // TODO weight should be double!!
    val weight: Long?,
    val completed: Boolean,
)

val mockSeries =
    Series(
        0,
        repetitions = 3,
        weight = 40,
        completed = false,
    )

val mockBlock =
    Block(
        idx = 0,
        exercise = mockExercise,
        series = listOf(mockSeries),
        restTimeSeconds = 69,
    )

val mockWorkout =
    Workout(
        id = "mock",
        userId = "",
        blocks = listOf(mockBlock, mockBlock),
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    )
