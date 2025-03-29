package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: String,
    val blocks: List<Block>,
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val completed: Boolean = false,
    val inProgress: Boolean = false,
) {
    // aggregates all completed series of an exercise
    // if there are no valid series of exercise in workout, return null
    private fun getExerciseSeries(exerciseId: String): List<Series>? = blocks
        .filter { it.exercise.id == exerciseId }
        .flatMap { it.series }
        .filter { it.completed }
        .ifEmpty { null }

    fun getExerciseHeaviestWeight(exerciseId: String): Double? {
        val series = getExerciseSeries(exerciseId) ?: return null
        val heaviestSet = series.maxByOrNull { if (it.repetitions == 0L) return 0.0 else it.weight ?: 0.0 }
        return heaviestSet?.weight
    }

    fun getExerciseBestSetVolume(exerciseId: String): Double? {
        val series = getExerciseSeries(exerciseId) ?: return null
        val bestVolume = series.mapNotNull { it.volume }.maxOrNull()
        return bestVolume
    }

    fun getExerciseTotalWorkoutVolume(exerciseId: String): Double? {
        val series = getExerciseSeries(exerciseId) ?: return null
        val totalVolume = series.mapNotNull { it.volume }.sum()
        return totalVolume
    }

    fun getExerciseTotalRepetitions(exerciseId: String): Long? {
        val series = getExerciseSeries(exerciseId) ?: return null
        val reps = series.sumOf { it.repetitions ?: 0 }
        return reps
    }

    val error: Error? get() = when {
        blocks.isEmpty() -> Error.NoExerciseInWorkout
        blocks.any { block -> block.series.isEmpty() } -> Error.NoSetInExercise
        blocks.any { block -> block.series.any { series -> series.weight == null || series.repetitions == null } } -> Error.EmptySetInExercise
        else -> null
    }

    sealed class Error(val message: String) {
        object NoExerciseInWorkout: Error("Workout has no exercises")
        object NoSetInExercise: Error("Some exercise has no sets")
        object EmptySetInExercise: Error("Some set has invalid values")
    }
}

@Serializable
data class WorkoutPlan(
    val name: String = "",
    val idx: Int,
    val blocks: List<Block>,
) {
    fun toWorkout() = Workout(
        id = "",
        blocks = blocks,
        completed = false,
        inProgress = false,
    )
}

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
    val weight: Double?,
    val completed: Boolean,
) {
    val volume = if (weight == null || repetitions == null) null else weight * repetitions
}

val mockSeries =
    Series(
        0,
        repetitions = 3,
        weight = 40.0,
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
        blocks = listOf(mockBlock, mockBlock),
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    )
