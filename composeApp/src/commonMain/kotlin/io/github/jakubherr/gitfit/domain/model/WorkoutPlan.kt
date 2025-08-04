package io.github.jakubherr.gitfit.domain.model

import io.github.jakubherr.gitfit.domain.model.Workout.Error
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutPlan(
    val name: String = "",
    val idx: Int,
    val blocks: List<Block>,
) {
    val error: Error? get() =
        when {
            name.isBlank() -> Error.BlankName
            blocks.isEmpty() -> Error.NoExerciseInWorkout
            blocks.any { block -> block.series.isEmpty() } -> Error.NoSetInExercise
            blocks.any { block ->
                block.series.any { series -> series.weight == null || series.repetitions == null }
            } -> Error.InvalidSetInExercise
            else -> null
        }

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

    // check workout record for valid progression and update plan accordingly
    //  Some restrictions were made on editing workout records with progression to prevent user from shooting themselves in the foot
    //  progression can NOT be changed mid-workout
    //  user can NOT remove block with progression
    //  user CAN add a new block without a progression -> OK
    //  user can NOT change order of exercises
    fun progressPlan(workoutRecord: Workout): WorkoutPlan {
        // filter out all blocks in workout record that have progression. if none are found, exit
        val blocksWithProgression = workoutRecord.blocks.filter { it.progressionSettings != null }.ifEmpty { return this }
        var workoutPlanCopy = this

        // check both record and plan have the same amount of progression blocks at the same indexes
        // if this check fails, the record is too different from the plan and progression will not happen
        val planProgressionBlocks = workoutPlanCopy.blocks.filter { it.progressionSettings != null }
        if (blocksWithProgression.map { it.idx } != planProgressionBlocks.map { it.idx }) return this

        // check every recorded block with progression for progress threshold criteria
        blocksWithProgression.forEach { recordedBlock ->
            val settings = recordedBlock.progressionSettings!!
            val shouldProgress =
                recordedBlock.series.all { series ->
                    series.completed &&
                        series.isNotNull &&
                        series.weight!! >= settings.weightThreshold &&
                        series.repetitions!! >= settings.repThreshold
                }

            // if criteria was met
            if (shouldProgress) {
                val planBlock = workoutPlanCopy.blocks[recordedBlock.idx]

                // increment all block weight/reps by increment
                // increment value in progression setting
                // save block to workout plan and then save it to plan
                when (settings.type) {
                    ProgressionType.INCREASE_WEIGHT -> workoutPlanCopy = workoutPlanCopy.updateBlock(planBlock.progressWeight())
                    ProgressionType.INCREASE_REPS -> workoutPlanCopy = workoutPlanCopy.updateBlock(planBlock.progressReps())
                }
            }
        }

        return workoutPlanCopy
    }

    companion object {
        fun empty(idx: Int) =
            WorkoutPlan(
                "New workout",
                idx,
                emptyList(),
            )
    }
}
