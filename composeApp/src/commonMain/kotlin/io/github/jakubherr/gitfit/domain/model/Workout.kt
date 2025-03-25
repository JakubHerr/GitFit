package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

data class Workout(
    val id: String,
    val userId: String,
    val name: String = "",
    val blocks: List<Block>,
    val date: LocalDate,
    val completed: Boolean = false,
    val inProgress: Boolean = false,
)

data class WorkoutPlan(
    val name: String = "",
    val idx: Int,
    val blocks: List<Block>,
)

@Serializable
data class Block(
    // TODO consider using index since it is inside a subcollection anyway
    val id: String,
    val idx: Int,
    val exercise: Exercise,
    val series: List<Series> = emptyList(),
    val restTimeSeconds: Long? = null,
)

// this should be named Set but is named series because of name conflict with Set collection
@Serializable
data class Series(
    val id: String,
    val repetitions: Long?,
    // TODO weight should be double
    val weight: Long?,
    val completed: Boolean,
)

val mockSeries =
    Series(
        id = "mock",
        repetitions = 3,
        weight = 40,
        completed = false,
    )

val mockBlock =
    Block(
        id = "mock",
        idx = 0,
        exercise = mockExercise,
        series = listOf(mockSeries),
        restTimeSeconds = 69,
    )

val mockWorkout =
    Workout(
        id = "mock",
        userId = "",
        blocks = listOf(mockBlock, mockBlock),
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    )
