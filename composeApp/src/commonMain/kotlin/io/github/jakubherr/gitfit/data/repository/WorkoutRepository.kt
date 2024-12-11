package io.github.jakubherr.gitfit.data.repository

import io.github.jakubherr.gitfit.data.source.LocalWorkoutDataSource
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.mockExercise
import io.github.jakubherr.gitfit.domain.mockWorkout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class WorkoutRepositoryImpl(
    private val localSource: LocalWorkoutDataSource, // TODO use interface once necessary
    // private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : WorkoutRepository {
    // private var list = mutableListOf<Workout>()

    override fun observeCurrentWorkout() = localSource.observeCurrentWorkoutOrNull().map { workout ->
        println("DBG: Repo observed workout $workout")
        if (workout == null) return@map mockWorkout

        localSource.observeBlocksForWorkout(workout.workoutId)

        Workout(
            workout.workoutId,
            localSource.getBlocksForWorkout(workout.workoutId).map { block ->
                Block(
                    block.blockId,
                    mockExercise, // TODO replace
                    localSource.getSeriesForBlock(block.blockId).map { series ->
                        Series(
                            series.seriesId,
                            series.reps,
                            series.weight.toLong(), // TODO fix
                            false
                        )
                    },
                    null
                )
            },
            LocalDate.parse(workout.date)
        )
    }

    override fun observeCurrentWorkoutOrNull(): Flow<Workout?> = localSource.assembleCurrentWorkoutOrNull()

    override suspend fun debug() {
        withContext(Dispatchers.IO) {
            localSource.debug()
        }
    }

    override suspend fun startWorkout() {
        println("DBG: Starting new workout...")
        localSource.startWorkout()
    }

    override suspend fun addBlock(workoutId: Long, exerciseId: Long) {
        println("DBG: Adding block with exercise $exerciseId to workout $workoutId")
        withContext(Dispatchers.IO) {
            localSource.addBlock(workoutId, exerciseId)
        }
    }

    override suspend fun removeBlock() {
        TODO("Not yet implemented")
    }

    override suspend fun setBlockTimer(block: Block, seconds: Long?) {
        TODO("Not yet implemented")
    }

    override suspend fun addEmptySeries(blockId: Long) {
        localSource.addEmptySeries(blockId)
    }

    override suspend fun modifySeries(set: Series) {
        TODO("Not yet implemented")
    }
}