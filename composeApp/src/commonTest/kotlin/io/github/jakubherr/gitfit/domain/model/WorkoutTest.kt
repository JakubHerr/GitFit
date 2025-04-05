package io.github.jakubherr.gitfit.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkoutTest {
    private val testExercise = Exercise("exerciseId", "exerciseName", primaryMuscle = MuscleGroup.CHEST)

    @Test
    fun whenWorkoutDoesNotContainExerciseReturnNull() {
        // given a workout with a valid exercise with completed sets
        val workout = Workout(
            id = "id",
            blocks = listOf(
                Block(
                    idx = 0,
                    testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 10,
                            weight = 70.0,
                            completed = true
                        )
                    )
                )
            )
        )

        // metrics for another exercise that is not in the workout should be null
        assertNull(workout.getExerciseTotalWorkoutVolume("differentExerciseId"))
    }

    @Test
    fun ignoreUncompletedSeriesInCalculations() {
        // given a workout with an exercise with one completed and one uncompleted series
        val workout = Workout(
            id = "id",
            blocks = listOf(
                Block(
                    idx = 0,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 12,
                            weight = 20.0,
                            completed = true
                        ),
                        Series(
                            idx = 1,
                            repetitions = 21,
                            weight = 35.0,
                            completed = false
                        )
                    )
                )
            )
        )

        // uncompleted series should not impact metric calculations
        assertEquals(20.0, workout.getExerciseHeaviestWeight(testExercise.id))
        assertEquals(240.0, workout.getExerciseBestSetVolume(testExercise.id))
        assertEquals(240.0, workout.getExerciseTotalWorkoutVolume(testExercise.id))
        assertEquals(12, workout.getExerciseTotalRepetitions(testExercise.id))
    }

    @Test
    fun ignoreSeriesWithMissingValues() {
        // given a workout with an exercise that has one valid series and one with missing values
        val workout = Workout(
            id = "id",
            blocks = listOf(
                Block(
                    idx = 0,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = null,
                            weight = 35.0,
                            completed = true
                        ),
                        Series(
                            idx = 1,
                            repetitions = 21,
                            weight = 20.0,
                            completed = true
                        )
                    )
                )
            )
        )

        // broken and invalid series should not impact calculations
        assertEquals(20.0, workout.getExerciseHeaviestWeight(testExercise.id))
        assertEquals(420.0, workout.getExerciseBestSetVolume(testExercise.id))
        assertEquals(420.0, workout.getExerciseTotalWorkoutVolume(testExercise.id))
        assertEquals(21, workout.getExerciseTotalRepetitions(testExercise.id))
    }

    @Test
    fun calculationsWorkAcrossBlocks() {
        // given workout with two blocks with the same exercise, with valid completed series in both blocks
        val workout = Workout(
            id = "id",
            blocks = listOf(
                Block(
                    idx = 0,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 10,
                            weight = 35.0,
                            completed = true
                        ),
                        Series(
                            idx = 1,
                            repetitions = 20,
                            weight = 20.0,
                            completed = true
                        )
                    )
                ),
                Block(
                    idx = 1,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 22,
                            weight = 35.0,
                            completed = true
                        ),
                        Series(
                            idx = 1,
                            repetitions = 3,
                            weight = 40.0,
                            completed = true
                        )
                    )
                )
            )
        )

        // metric calculations aggregate data from both blocks
        assertEquals(40.0, workout.getExerciseHeaviestWeight(testExercise.id))
        assertEquals(770.0, workout.getExerciseBestSetVolume(testExercise.id))
        assertEquals(1640.0, workout.getExerciseTotalWorkoutVolume(testExercise.id))
        assertEquals(55, workout.getExerciseTotalRepetitions(testExercise.id))
    }

    @Test
    fun heaviestWeightCalculationIgnoresSeriesWith0Repetitions() {
        // given a workout with an exercise with a series with heavier weight but no repetitions
        val workout = Workout(
            id = "id",
            blocks = listOf(
                Block(
                    idx = 0,
                    exercise = testExercise,
                    series = listOf(
                        Series(
                            idx = 0,
                            repetitions = 0,
                            weight = 100.0,
                            completed = true
                        ),
                        Series(
                            idx = 1,
                            repetitions = 12,
                            weight = 70.0,
                            completed = true
                        )
                    )
                )
            )
        )

        // the heavier series is ignored and lighter series is selected
        assertEquals(70.0, workout.getExerciseHeaviestWeight(testExercise.id))
    }

    @Test
    fun outOfOrderExerciseDeletionWorks() {
        // given workout with three exercise blocks
        var workout = Workout(
            id = "id",
            blocks = listOf(
                Block(
                    idx = 0,
                    exercise = testExercise
                ),
                Block(
                    idx = 1,
                    exercise = Exercise("middleId", "middle exercise", primaryMuscle = MuscleGroup.LEGS)
                ),
                Block(
                    idx = 2,
                    exercise = Exercise("lastId", "last exercise", primaryMuscle = MuscleGroup.SHOULDERS)
                )
            )
        )

        // when exercise is deleted from the middle
        workout = workout.removeBlock(1)

        // exercise block is removed and block indexes are updated
        assertFalse(workout.hasExercise("middleId"))
        assertTrue {
            workout.blocks.forEachIndexed { index, block ->
                if (block.idx != index) return@assertTrue false
            }
            true
        }
    }

    @Test
    fun errorCheckingWorks() {
        // given workout without exercise blocks
        var workout = Workout(
            id = "id",
            blocks = emptyList(),
        )
        assertEquals(Workout.Error.NoExerciseInWorkout, workout.error)

        // given workout with exercise block without series
        workout = workout.addBlock(Exercise("exerciseId", "exerciseName", primaryMuscle = MuscleGroup.CHEST))
        assertEquals(Workout.Error.NoSetInExercise, workout.error)

        // given workout with exercise block with empty series
        workout = workout.updateBlock(workout.blocks.first().addSeries())
        assertEquals(Workout.Error.InvalidSetInExercise, workout.error)

        // given workout with exercise block with series that is missing values
        workout = workout.updateBlock(
            workout.blocks.first().updateSeries(
                workout.blocks.first().series.first().copy(
                    weight = 20.5
                )
            )
        )
        assertEquals(Workout.Error.InvalidSetInExercise, workout.error)

        // given workout with one exercise with one series with valid series
        workout = workout.updateBlock(
            workout.blocks.first().updateSeries(
                workout.blocks.first().series.first().copy(
                    weight = 20.5, repetitions = 5
                )
            )
        )
        assertNull(workout.error)
    }
}
