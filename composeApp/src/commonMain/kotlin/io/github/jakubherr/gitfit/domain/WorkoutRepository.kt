package io.github.jakubherr.gitfit.domain

import kotlinx.coroutines.flow.Flow


interface WorkoutRepository {
    fun observeCurrentWorkoutOrNull(): Flow<Workout?>

    suspend fun startNewWorkout()
    suspend fun startPlannedWorkout(workoutId: String)
    suspend fun completeWorkout(workoutId: String)
    suspend fun deleteWorkout(workoutId: String)

    suspend fun addBlock(workoutId: String, exerciseId: String)
    suspend fun removeBlock(workoutId: String, blockId: String)
    suspend fun setBlockTimer(blockId: String, seconds: Long?)

    suspend fun addSeries(workoutId: String, blockId: String, set: Series)
    // TODO remove series
    suspend fun modifySeries(set: Series)
    suspend fun toggleSeries(seriesId: String)

    // TODO get only users workouts, pagination
    suspend fun getCompletedWorkouts()
    suspend fun getPlannedWorkouts()
}