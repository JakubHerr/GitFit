package io.github.jakubherr.gitfit.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkoutPlanTest {
    private val testExercise = Exercise("exerciseId", "exerciseName", primaryMuscle = MuscleGroup.CHEST)

    private val originalPlan = WorkoutPlan(
        idx = 0,
        name = "original",
        blocks = listOf(
            Block(
                idx = 0,
                exercise = testExercise,
                series = listOf(
                    Series(
                        idx = 0,
                        repetitions = 10,
                        weight = 50.0,
                        completed = false
                    )
                ),
                progressionSettings = null
            ),
            Block(
                idx = 1,
                exercise = testExercise,
                series = listOf(
                    Series(
                        idx = 0,
                        repetitions = 5,
                        weight = 20.0,
                        completed = false
                    ),
                    Series(
                        idx = 1,
                        repetitions = 5,
                        weight = 20.0,
                        completed = false
                    )
                ),
                progressionSettings = ProgressionSettings(
                    type = ProgressionType.INCREASE_WEIGHT,
                    trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                    incrementWeightByKg = 2.5,
                    incrementRepsBy = 0,
                    weightThreshold = 20.0,
                    repThreshold = 5,
                )
            )
        )
    )

    @Test
    fun validProgression() {
        // given a recorded workout with progression
        val workoutRecord = Workout(
            id = "workoutId",
            completed = true,
            inProgress = false,
            blocks = listOf(
                Block(
                    idx = 0,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 10,
                            weight = 60.0,
                            completed = true
                        )
                    ),
                    progressionSettings = null
                ),
                Block(
                    idx = 1,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 5,
                            weight = 20.0,
                            completed = true
                        ),
                        Series(
                            idx = 1,
                            repetitions = 5,
                            weight = 20.0,
                            completed = true
                        )
                    ),
                    progressionSettings = ProgressionSettings(
                        type = ProgressionType.INCREASE_WEIGHT,
                        trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                        incrementWeightByKg = 2.5,
                        incrementRepsBy = 0,
                        weightThreshold = 20.0,
                        repThreshold = 5,
                    )
                )
            )
        )

        // when user progresses a plan from a workout record
        val updatedPlan = originalPlan.progressPlan(workoutRecord)

        // then blocks with completed progression criteria get progressed
        assertEquals(22.5, updatedPlan.blocks[1].progressionSettings?.weightThreshold)
        assertEquals(5, updatedPlan.blocks[1].progressionSettings?.repThreshold)
        assertTrue {
            updatedPlan.blocks[1].series.all { it.weight == 22.5 && it.repetitions == 5L }
        }

        // and blocks without progression stay the same
        assertTrue {
            updatedPlan.blocks[0].series.all { it.weight == 50.0 && it.repetitions == 10L }
        }
    }

    @Test
    fun invalidProgression() {
        // given workout record based on workout plan, where user changed ordering of blocks with progression
        val workoutRecord = Workout(
            id = "workoutId",
            completed = true,
            inProgress = false,
            blocks = listOf(
                Block(
                    idx = 0,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 5,
                            weight = 20.0,
                            completed = true
                        ),
                        Series(
                            idx = 1,
                            repetitions = 5,
                            weight = 20.0,
                            completed = true
                        )
                    ),
                    progressionSettings = ProgressionSettings(
                        type = ProgressionType.INCREASE_WEIGHT,
                        trigger = ProgressionTrigger.MINIMUM_REPS_AND_WEIGHT_EVERY_SET,
                        incrementWeightByKg = 2.5,
                        incrementRepsBy = 0,
                        weightThreshold = 20.0,
                        repThreshold = 5,
                    )
                ),
                Block(
                    idx = 1,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 10,
                            weight = 60.0,
                            completed = true
                        )
                    ),
                    progressionSettings = null
                )
            )
        )

        // when the plan is progressed
        val progressedPlan = originalPlan.progressPlan(workoutRecord)

        // then nothing happens, because the record differs from the plan too much
        assertEquals(originalPlan, progressedPlan)
    }
}
