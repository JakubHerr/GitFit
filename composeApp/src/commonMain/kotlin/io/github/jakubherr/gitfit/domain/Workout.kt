package io.github.jakubherr.gitfit.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

data class Workout(
    val id: String,
    val blocks: List<Block>,
    val date: LocalDate,
    val completed: Boolean = false,
    val inProgress: Boolean = false,
)

@Serializable
data class Block(
    val id: String,
    val exercise: Exercise,
    val series: List<Series> = emptyList(),
    val restTimeSeconds: Long?
)

// this should be named Set but is named series because of name conflict with Set collection
@Serializable
data class Series(
    val id: String,
    val repetitions: Long?,
    val weight: Long?,
    val completed: Boolean
)

val mockSeries = Series(
    id = "mock",
    repetitions = 3,
    weight = 40,
    completed = false
)

val mockBlock = Block(
    id = "mock",
    exercise = mockExercise,
    series = listOf(mockSeries),
    restTimeSeconds = 69,
)

val mockWorkout = Workout(
    id = "mock",
    blocks = listOf(mockBlock, mockBlock),
    date = Clock.System.todayIn(TimeZone.currentSystemDefault())
)

