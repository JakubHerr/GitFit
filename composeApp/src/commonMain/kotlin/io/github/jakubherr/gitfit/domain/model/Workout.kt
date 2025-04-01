package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

// Important: never rename existing variable names, it will create problems with existing records in database
// TODO solution: Add abstraction to data layer that will translate domain models to DTO

@Serializable
data class Workout(
    val id: String,
    val blocks: List<Block>,
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val completed: Boolean = false,
    val inProgress: Boolean = false,
    val planId: String? = null,
    val planWorkoutIdx: Int? = null,
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

val mockWorkout =
    Workout(
        id = "mock",
        blocks = listOf(mockBlock, mockBlock),
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    )
