package io.github.jakubherr.gitfit.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BlockTest {
    private val testExercise = Exercise("exerciseId", "exerciseName", primaryMuscle = MuscleGroup.CHEST)

    @Test
    fun weightProgressesWhenItShould() {
        // given a completed block with valid weight progression settings that meet progression criteria
        val originalBlock = Block(
            idx = 0,
            exercise = testExercise,
            series = listOf(
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 25.0,
                    completed = true
                ),
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 20.0,
                    completed = true
                )
            ),
            progressionSettings = ProgressionSettings(
                incrementWeightByKg = 5.12,
                type = ProgressionType.INCREASE_WEIGHT,
                trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                incrementRepsBy = 0,
                weightThreshold = 20.0,
                repThreshold = 10,
            )
        )

        // when block is progressed
        val updatedBlock = originalBlock.progressWeight()

        // then all weights in block are increased by 5.12 kg and repetitions stay the same
        assertTrue {
            updatedBlock.series[0].weight == 30.12 &&
            updatedBlock.series[0].repetitions == 10L &&
            updatedBlock.series[1].weight == 25.12 &&
            updatedBlock.series[1].repetitions == 10L
        }
        // and progression setting weight threshold is updated for the next progression
        assertTrue {
            updatedBlock.progressionSettings?.weightThreshold == 25.12
        }
    }

    @Test
    fun repetitionProgressesWhenItShould() {
        // given a completed block with valid repetition progression settings that meet progression criteria
        val originalBlock = Block(
            idx = 0,
            exercise = testExercise,
            series = listOf(
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 25.0,
                    completed = true
                ),
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 20.0,
                    completed = true
                )
            ),
            progressionSettings = ProgressionSettings(
                incrementWeightByKg = 0.0,
                type = ProgressionType.INCREASE_REPS,
                trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                incrementRepsBy = 2,
                weightThreshold = 20.0,
                repThreshold = 10,
            )
        )

        // when block is updated
        val updatedBlock = originalBlock.progressReps()

        // then all repetitions in the block are updated and weight stays the same
        assertTrue {
            updatedBlock.series[0].repetitions == 12L &&
            updatedBlock.series[0].weight == 25.00 &&
            updatedBlock.series[1].repetitions == 12L &&
            updatedBlock.series[1].weight == 20.0
        }
        // and progression setting repetition threshold is updated for the next progression
        assertTrue { updatedBlock.progressionSettings?.repThreshold == 12 }
    }

    @Test
    fun weightStaysTheSameWhenItShould() {
        // given a block where user did not meet progression criteria
        val originalBlock = Block(
            idx = 0,
            exercise = testExercise,
            series = listOf(
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 25.0,
                    completed = true
                ),
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 20.0,
                    completed = true
                )
            ),
            progressionSettings = ProgressionSettings(
                incrementWeightByKg = 5.00,
                type = ProgressionType.INCREASE_WEIGHT,
                trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                incrementRepsBy = 0,
                weightThreshold = 44.0,
                repThreshold = 10,
            )
        )

        // when weight is progressed
        val updatedBlock = originalBlock.progressWeight()

        // then the block stays the same
        assertEquals(updatedBlock, originalBlock)
    }

    @Test
    fun repetitionsStayTheSameWhenTheyShould() {
        // given a block where user did not meet progression criteria
        val originalBlock = Block(
            idx = 0,
            exercise = testExercise,
            series = listOf(
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 25.0,
                    completed = true
                ),
                Series(
                    idx = 0,
                    repetitions = 10,
                    weight = 20.0,
                    completed = true
                )
            ),
            progressionSettings = ProgressionSettings(
                incrementWeightByKg = 0.0,
                type = ProgressionType.INCREASE_REPS,
                trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                incrementRepsBy = 2,
                weightThreshold = 20.0,
                repThreshold = 15,
            )
        )

        // when repetitions are progressed
        val updatedBlock = originalBlock.progressReps()

        // then the block stays the same
        assertEquals(originalBlock, updatedBlock)
    }
}