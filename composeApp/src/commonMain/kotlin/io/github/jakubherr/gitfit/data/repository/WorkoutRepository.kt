package io.github.jakubherr.gitfit.data.repository

import io.github.jakubherr.gitfit.data.source.LocalWorkoutDataSource
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.mockExercise
import io.github.jakubherr.gitfit.domain.mockWorkout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
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

        localSource.observeBlocksForWorkout(workout.id)

        Workout(
            workout.id,
            localSource.getBlocksForWorkout(workout.id).map { block ->
                Block(
                    block.id,
                    mockExercise, // TODO replace
                    localSource.getSeriesForBlock(block.id).map { series ->
                        Series(
                            series.id,
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

    fun observeCurrentWorkout2() = localSource.assembleCurrentWorkoutOrNull().map { query ->

    }

    override suspend fun debug() {
        withContext(Dispatchers.IO) {
            localSource.debug()
        }
    }

    override suspend fun startWorkout() {
        localSource.startWorkout()
    }

    override suspend fun addBlock(workoutId: Long, exerciseId: Long) {
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

    override suspend fun addEmptySeries(block: Block) {
        TODO("Not yet implemented")
    }

    override suspend fun modifySeries(set: Series) {
        TODO("Not yet implemented")
    }
}