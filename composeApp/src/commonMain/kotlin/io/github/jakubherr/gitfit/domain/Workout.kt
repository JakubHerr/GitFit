package io.github.jakubherr.gitfit.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

// a workout could be saved in progress
@Serializable
data class Workout(
    val id: Long,
    val blocks: List<Block>,
    val date: LocalDate,
)

@Serializable
data class Block(
    val id: Long,
    val exercise: Exercise,
    val series: List<Series>,
    val restTimeSeconds: Long?
)

// this should be named Set but is named series because of name conflict with Set collection
@Serializable
data class Series(
    val id: Long,
    val repetitions: Long?,
    val weight: Long?,
    val completed: Boolean
)

val mockSeries = Series(
    id = -1,
    repetitions = 3,
    weight = 40,
    completed = false
)

val mockBlock = Block(
    id = -1,
    exercise = mockExercise,
    series = listOf(mockSeries),
    restTimeSeconds = 69,
)

val mockWorkout = Workout(
    id = -1,
    blocks = listOf(mockBlock, mockBlock),
    date = Clock.System.todayIn(TimeZone.currentSystemDefault())
)

