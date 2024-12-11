package io.github.jakubherr.gitfit.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import io.github.jakubherr.gitfit.data.mapper.toExercise
import io.github.jakubherr.gitfit.db.LocalDatabase
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn


class LocalWorkoutDataSource(
    private val db: LocalDatabase,
) {
    private val workouts = db.workoutQueries
    private val blocks = db.blockQueries
    private val sets = db.seriesQueries
    private val exercise = db.exerciseQueries

    suspend fun debug() {
            assembleCurrentWorkoutOrNull().collect {
                println("DBG: Detected Workout: ${it.toString()}")
                println("DBG: block ${it?.blocks?.joinToString()}")
            }

            workouts.getCurrent().asFlow().mapToOneOrNull(Dispatchers.IO).collect { workout ->
                println("DBG: Current workout: $workout")
                workout?.let {
                    val blocks = getBlocksForWorkout(it.workoutId)
                    println("DBG: Blocks in workout: ${blocks.joinToString()}")
                }
            }
    }

    fun observeCurrentWorkoutOrNull() = workouts.getCurrent().asFlow().mapToOneOrNull(Dispatchers.IO)

    fun assembleCurrentWorkoutOrNull() = workouts.getCurrentWithSeries().asFlow().mapToList(Dispatchers.IO).map { blob ->
        if (blob.isEmpty()) null else {
            val block = blob.filter { it.blockId != null }.groupBy { it.blockId }.map { (id, u) ->
                    val firstResult = u.first()

                    Block(
                        id = firstResult.blockId!!,
                        exercise = exercise.selectById(firstResult.exerciseFk!!, mapper = ::toExercise).executeAsOne(),
                        u.filterNot { it.seriesId == null }.map {
                            Series(
                                it.seriesId!!,
                                it.reps,
                                it.weight?.toLong(),
                                it.completed
                            )
                        },
                        null
                    )
                }

            Workout(
                blob.first().workoutId,
                block.sortedBy { it.id },
                LocalDate.parse(blob.first().date),
            )
        }
    }

    suspend fun startWorkout() {
        workouts.insert("", false, Clock.System.todayIn(TimeZone.currentSystemDefault()).toString())
    }

    fun getBlocksForWorkout(workoutId: Long) = blocks.getForWorkout(workoutId).executeAsList()

    fun observeBlocksForWorkout(workoutId: Long) = blocks.getForWorkout(workoutId).asFlow().mapToList(Dispatchers.IO)

    fun getSeriesForBlock(blockId: Long) = sets.getForBlock(blockId).executeAsList()

    suspend fun addBlock(workoutId: Long, exerciseId: Long) {
        workouts.transaction {
            blocks.insert(workoutId, exerciseId)
        }
    }

    suspend fun addEmptySeries(blockId: Long) {
        println("DBG: adding empty series to block $blockId")
        sets.insert(blockId, 0.0, 0)
    }
}