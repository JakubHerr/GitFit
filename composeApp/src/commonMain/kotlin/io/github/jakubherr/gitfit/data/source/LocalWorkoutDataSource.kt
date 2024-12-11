package io.github.jakubherr.gitfit.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import io.github.jakubherr.gitfit.db.LocalDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn


class LocalWorkoutDataSource(
    private val db: LocalDatabase,
) {
    private val workouts = db.workoutQueries
    private val blocks = db.blockQueries
    private val sets = db.seriesQueries

    suspend fun debug() {
            workouts.getCurrent().asFlow().mapToOneOrNull(Dispatchers.IO).collect { workout ->
                println("DBG: Current workout: $workout")
                workout?.let {
                    val blocks = getBlocksForWorkout(it.id)
                    println("DBG: Blocks in workout: ${blocks.joinToString()}")
                }
            }
    }

    fun observeCurrentWorkoutOrNull() = workouts.getCurrent().asFlow().mapToOneOrNull(Dispatchers.IO)

    fun assembleCurrentWorkoutOrNull() = workouts.getCurrentWithSeries().asFlow().mapToOneOrNull(Dispatchers.IO)

    suspend fun startWorkout() {
        workouts.insert("", false, Clock.System.todayIn(TimeZone.currentSystemDefault()).toString())
    }

    fun getBlocksForWorkout(workoutId: Long) = blocks.getForWorkout(workoutId).executeAsList()

    fun observeBlocksForWorkout(workoutId: Long) = blocks.getForWorkout(workoutId).asFlow().mapToList(Dispatchers.IO)

    fun getSeriesForBlock(blockId: Long) = sets.getForBlock(blockId).executeAsList()

    suspend fun addBlock(workoutId: Long, exerciseId: Long) {
        blocks.insert(workoutId, exerciseId)
    }
}