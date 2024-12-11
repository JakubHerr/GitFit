package io.github.jakubherr.gitfit.domain

import kotlinx.coroutines.flow.Flow

// should be able to add a block with exercise, add a set, add weight and reps to set, complete set, add timer to block
interface WorkoutRepository {
    suspend fun startWorkout()
    suspend fun addBlock(workoutId: Long, exerciseId: Long)
    suspend fun removeBlock()
    suspend fun setBlockTimer(block: Block, seconds: Long?)

    suspend fun addEmptySeries(block: Block) // maybe not necessary, just add series when weight and/or reps are added?
    suspend fun modifySeries(set: Series)

    fun observeCurrentWorkout(): Flow<Workout>

    suspend fun debug()
}