package io.github.jakubherr.gitfit.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlanTest {
    private val testExercise = Exercise("exerciseId", "exerciseName", primaryMuscle = MuscleGroup.CHEST)

    @Test
    fun errorCheckingWorks() {
        // given plan with blank name
        var plan =
            Plan(
                id = "testPlan",
                userId = "testUser",
                name = "",
                description = "",
            )
        assertEquals(Plan.Error.InvalidPlanName, plan.error)

        // given named plan with no workout days
        plan = plan.copy(name = "Named plan")
        assertEquals(Plan.Error.NoWorkoutInPlan, plan.error)

        // given plan with workout with no exercises
        plan = plan.addWorkoutPlan(WorkoutPlan.empty(plan.workoutPlans.size))
        assertTrue {
            val error = plan.error
            error is Plan.Error.InvalidWorkout && error.error is Workout.Error.NoExerciseInWorkout
        }

        // given plan with workout with exercise with no series
        plan = plan.addExercise(0, testExercise)
        assertTrue {
            val error = plan.error
            error is Plan.Error.InvalidWorkout && error.error is Workout.Error.NoSetInExercise
        }

        // given plan with workout with exercise with empty series
        val workoutPlan1 = plan.workoutPlans[0]
        plan = plan.addSeries(workoutPlan1, workoutPlan1.blocks[0])
        assertTrue {
            val error = plan.error
            error is Plan.Error.InvalidWorkout && error.error is Workout.Error.InvalidSetInExercise
        }

        // given plan with valid workout with blank name
        val workoutPlan2 = plan.workoutPlans[0]
        plan = plan.updateSeries(workoutPlan2, workoutPlan2.blocks[0], Series(0, 5, 10.0, true))
        plan = plan.updateWorkoutPlan(plan.workoutPlans.first().copy(name = ""))
        assertTrue {
            val error = plan.error
            error is Plan.Error.InvalidWorkout && error.error is Workout.Error.BlankName
        }

        // given plan with valid workout and valid name
        val workoutPlan3 = plan.workoutPlans[0]
        plan = plan.updateWorkoutPlan(workoutPlan3.copy(name = "named workout"))
        assertNull(plan.error)
    }

    @Test
    fun workoutPlanCRUD() {
        // given empty plan
        var plan =
            Plan(
                id = "testPlan",
                userId = "testUser",
                name = "plan name",
                description = "",
                workoutPlans = emptyList(),
            )

        // workout plans can be added
        plan =
            plan
                .addWorkoutPlan(WorkoutPlan.empty(0))
                .addWorkoutPlan(WorkoutPlan.empty(1))
                .addWorkoutPlan(WorkoutPlan.empty(2))
        assertEquals(3, plan.workoutPlans.size)

        // workout plans can be updated
        plan = plan.updateWorkoutPlan(plan.workoutPlans.first().copy("updated workout name"))
        assertEquals("updated workout name", plan.workoutPlans.first().name)

        // workout plans can be deleted, even out of order
        plan = plan.removeWorkoutPlan(plan.workoutPlans[1])
        assertEquals(2, plan.workoutPlans.size)
        assertTrue {
            plan.workoutPlans.forEachIndexed { index, workoutPlan ->
                if (workoutPlan.idx != index) return@assertTrue false
            }
            true
        }
    }

    // this test also indirectly validates WorkoutPlan
    @Test
    fun exerciseCRUD() {
        // given plan with empty workout day
        var plan =
            Plan(
                id = "testPlan",
                userId = "testUser",
                name = "plan name",
                description = "",
                workoutPlans =
                    listOf(
                        WorkoutPlan(
                            name = "workout plan",
                            idx = 0,
                            blocks = emptyList(),
                        ),
                    ),
            )

        // exercise blocks can be added
        plan =
            plan
                .addExercise(0, testExercise)
                .addExercise(0, testExercise)
                .addExercise(0, testExercise)
        assertEquals(3, plan.workoutPlans[0].blocks.size)

        // exercise blocks can be removed, even out of order
        plan = plan.removeBlock(plan.workoutPlans[0], plan.workoutPlans[0].blocks[1])
        assertEquals(2, plan.workoutPlans[0].blocks.size)
        assertTrue {
            plan.workoutPlans[0].blocks.forEachIndexed { index, block ->
                if (block.idx != index) return@assertTrue false
            }
            true
        }
    }

    // this test also indirectly validates WorkoutPlan, Block and Series
    @Test
    fun seriesCRUD() {
        // given plan with workout day with empty exercise
        var plan =
            Plan(
                id = "testPlan",
                userId = "testUser",
                name = "plan name",
                description = "",
                workoutPlans =
                    listOf(
                        WorkoutPlan(
                            name = "workout plan",
                            idx = 0,
                            blocks =
                                listOf(
                                    Block(
                                        idx = 0,
                                        testExercise,
                                        series = emptyList(),
                                    ),
                                ),
                        ),
                    ),
            )

        // series can be added
        plan = plan.addSeries(plan.workoutPlans[0], plan.workoutPlans[0].blocks[0])
        plan = plan.addSeries(plan.workoutPlans[0], plan.workoutPlans[0].blocks[0])
        plan = plan.addSeries(plan.workoutPlans[0], plan.workoutPlans[0].blocks[0])
        assertEquals(
            3,
            plan.workoutPlans[0]
                .blocks[0]
                .series.size,
        )

        // series can be updated
        val newSeries =
            plan.workoutPlans[0]
                .blocks[0]
                .series[0]
                .copy(weight = 80.0, repetitions = 12)
        plan =
            plan.updateSeries(
                plan.workoutPlans[0],
                plan.workoutPlans[0].blocks[0],
                newSeries,
            )
        assertEquals(newSeries, plan.workoutPlans[0].blocks[0].series[0])

        // series can be removed, even out of order
        plan =
            plan.removeSeries(
                plan.workoutPlans[0],
                plan.workoutPlans[0].blocks[0],
                plan.workoutPlans[0].blocks[0].series[1],
            )
        assertEquals(
            2,
            plan.workoutPlans[0]
                .blocks[0]
                .series.size,
        )
        assertTrue {
            plan.workoutPlans[0].blocks[0].series.forEachIndexed { index, series ->
                if (series.idx != index) return@assertTrue false
            }
            true
        }
    }
}
