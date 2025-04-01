package io.github.jakubherr.gitfit.domain.model

import kotlinx.serialization.Serializable

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
}