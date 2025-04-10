package io.github.jakubherr.gitfit.previews

import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Measurement
import io.github.jakubherr.gitfit.domain.model.MuscleGroup
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.domain.model.WorkoutPlan
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

val mockExercise =
    Exercise(
        "mock",
        "Bench press",
        "",
        false,
        MuscleGroup.CHEST,
        listOf(MuscleGroup.SHOULDERS),
    )

val mockSeries =
    Series(
        0,
        repetitions = 3,
        weight = 40.0,
        completed = false,
    )

val mockSeriesComplete =
    Series(
        1,
        repetitions = 3,
        weight = 40.0,
        completed = true,
    )

val mockBlockComplete =
    Block(
        idx = 0,
        exercise = mockExercise,
        series = listOf(mockSeriesComplete, mockSeriesComplete),
        restTimeSeconds = 69,
    )

val mockBlock =
    Block(
        idx = 0,
        exercise = mockExercise,
        series = listOf(mockSeries, mockSeries),
        restTimeSeconds = 69,
    )

val mockWorkout =
    Workout(
        id = "mock",
        blocks = listOf(mockBlockComplete, mockBlock),
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    )

val mockWorkoutPlan = WorkoutPlan(
    name = "Workout A",
    idx = 0,
    blocks = listOf(mockBlock)
)

val mockPlan = Plan(
    id = "mock",
    userId = null,
    name = "Mock plan",
    description = "no description",
    workoutPlans = listOf(mockWorkoutPlan, mockWorkoutPlan)
)

val mockMeasurement = Measurement(
    date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    1.0,
    2.0,
    3.0,
    4.0,
    5.0,
    6.0,
    7.0,
    8.0,
    9.0,
    10.0,
    11.0,
    12.0,
    13.0,
)
