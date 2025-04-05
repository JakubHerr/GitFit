package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

// Important: never rename existing variable names, it will create problems with existing records in database
// optional: Add abstraction to data layer that will translate domain models to DTO

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
    // aggregates all completed, valid series of an exercise
    // if there are no valid series of exercise in workout, return null
    private fun getExerciseSeries(exerciseId: String): List<Series>? = blocks
        .filter { it.exercise.id == exerciseId }
        .flatMap { it.series }
        .filter { it.completed && it.isNotNull }
        .ifEmpty { null }

    fun getExerciseHeaviestWeight(exerciseId: String): Double? {
        val series = getExerciseSeries(exerciseId) ?: return null
        val heaviestSet = series.maxByOrNull { if (it.repetitions == 0L) 0.0 else it.weight ?: 0.0 }
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

    fun addBlock(exercise: Exercise): Workout = copy(blocks = blocks + Block(blocks.size, exercise))

    fun updateBlock(block: Block): Workout {
        val newBlocks = blocks.toMutableList()
        newBlocks[block.idx] = block
        return copy(blocks = newBlocks)
    }

    fun removeBlock(blockIdx: Int): Workout {
        val newBlocks = blocks.toMutableList()
        newBlocks.removeAt(blockIdx)
        newBlocks.forEachIndexed { idx, oldBlock ->
            newBlocks[idx] = oldBlock.copy(idx = idx)
        }
        return copy(blocks = newBlocks)
    }

    val error: Error? get() = when {
        blocks.isEmpty() -> Error.NoExerciseInWorkout
        blocks.any { block -> block.series.isEmpty() } -> Error.NoSetInExercise
        blocks.any { block -> block.series.any { series -> series.weight == null || series.repetitions == null } } -> Error.InvalidSetInExercise
        else -> null
    }

    fun hasExercise(exerciseId: String?) = blocks.any { it.exercise.id == exerciseId }

    sealed class Error(val message: String) {
        object NoExerciseInWorkout: Error("Workout has no exercises")
        object NoSetInExercise: Error("Some exercise has no sets")
        object InvalidSetInExercise: Error("Some set has invalid values")
        object BlankName: Error("Workout plan has no name")
    }
}

val mockWorkout =
    Workout(
        id = "mock",
        blocks = listOf(mockBlock, mockBlock),
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    )
