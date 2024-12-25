package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.flow.Flow

// TODO integrate with auth to CRUD for a single user only, pagination
interface WorkoutRepository {
    fun observeCurrentWorkoutOrNull(): Flow<Workout?>

    suspend fun startNewWorkout()
    suspend fun startPlannedWorkout(workoutId: String)
    suspend fun completeWorkout(workoutId: String)
    suspend fun deleteWorkout(workoutId: String)

    suspend fun addBlock(workoutId: String, exerciseId: String)
    suspend fun removeBlock(workoutId: String, blockId: String)
    suspend fun setBlockTimer(workoutId: String, blockId: String, seconds: Long?)

    suspend fun addSeries(workoutId: String, blockId: String, set: Series)
    suspend fun modifySeries(workoutId: String, blockId: String, set: Series)
    suspend fun removeSeries(workoutId: String, blockId: String, set: Series)

    suspend fun getCompletedWorkouts()
    suspend fun getPlannedWorkouts(): List<Workout>
}