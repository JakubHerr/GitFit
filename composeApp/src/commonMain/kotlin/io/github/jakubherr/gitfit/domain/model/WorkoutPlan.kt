package io.github.jakubherr.gitfit.domain.model

import io.github.jakubherr.gitfit.domain.model.Workout.Error
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutPlan(
    val name: String = "",
    val idx: Int,
    val blocks: List<Block>,
) {
    val error: Error? get() = when {
        name.isBlank() -> Error.BlankName
        blocks.isEmpty() -> Error.NoExerciseInWorkout
        blocks.any { block -> block.series.isEmpty() } -> Error.NoSetInExercise
        blocks.any { block -> block.series.any { series -> series.weight == null || series.repetitions == null } } -> Error.EmptySetInExercise
        else -> null
    }

    fun toWorkout() = Workout(
        id = "",
        blocks = blocks,
        completed = false,
        inProgress = false,
    )

    fun addBlock(exercise: Exercise): WorkoutPlan = copy(blocks = blocks + Block(blocks.size, exercise))

    fun updateBlock(block: Block): WorkoutPlan {
        val newBlocks = blocks.toMutableList()
        newBlocks[block.idx] = block
        return copy(blocks = newBlocks)
    }

    fun removeBlock(block: Block): WorkoutPlan {
        val newBlocks = blocks.toMutableList()
        newBlocks.remove(block)
        newBlocks.forEachIndexed { idx, oldBlock ->
            newBlocks[idx] = oldBlock.copy(idx = idx)
        }
        return copy(blocks = newBlocks)
    }

    companion object {
        fun Empty(idx: Int) = WorkoutPlan(
            "New workout",
            idx,
            emptyList(),
        )
    }
}