package io.github.jakubherr.gitfit.domain

// a workout could be saved in progress
data class Workout(
    val id: Long,
    val blocks: List<Block>,
    // todo add date
)

data class Block(
    val id: Long,
    val exercise: Exercise,
    val series: List<Series>,
    val restTimeSeconds: Long?
)

// this should be named Set but is named series because of name conflict with Set collection
data class Series(
    val repetitions: Long?,
    val weight: Long?,
    val completed: Boolean
)

val mockSeries = Series(
    repetitions = 3,
    weight = 40,
    false
)

val mockBlock = Block(
    id = -1,
    exercise = mockExercise,
    series = listOf(mockSeries),
    restTimeSeconds = 69,
)

val mockWorkout = Workout(
    id = -1,
    blocks = listOf(mockBlock),
)

