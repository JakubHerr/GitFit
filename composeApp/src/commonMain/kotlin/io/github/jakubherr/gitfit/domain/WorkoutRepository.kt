package io.github.jakubherr.gitfit.domain

import kotlinx.coroutines.flow.Flow


interface WorkoutRepository {
    suspend fun startWorkout()
    suspend fun completeWorkout()

    // TODO add weight and reps to set
    //  add, modify, remove timer to block

    suspend fun addBlock(workoutId: Long, exerciseId: Long)
    suspend fun removeBlock()
    suspend fun setBlockTimer(block: Block, seconds: Long?)

    suspend fun addEmptySeries(blockId: Long) // maybe not necessary, just add series when weight and/or reps are added?
    suspend fun toggleSeries(seriesId: Long)
    suspend fun modifySeries(set: Series)

    fun observeCurrentWorkout(): Flow<Workout>

    fun observeCurrentWorkoutOrNull(): Flow<Workout?>

    suspend fun debug()
}