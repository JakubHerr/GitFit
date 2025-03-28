package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeCurrentWorkoutOrNull(): Flow<Workout?>

    suspend fun startNewWorkout()

    suspend fun startWorkoutFromPlan(planId: String, workoutIdx: Int)

    suspend fun completeWorkout(workoutId: String)

    suspend fun deleteWorkout(workoutId: String)

    suspend fun addBlock(
        workoutId: String,
        exerciseId: String,
    )

    suspend fun removeBlock(
        workoutId: String,
        blockIdx: Int,
    )

    suspend fun setBlockTimer(
        workoutId: String,
        blockIdx: Int,
        seconds: Long?,
    )

    suspend fun addSeries(
        workoutId: String,
        blockIdx: Int,
        set: Series,
    )

    suspend fun modifySeries(
        workoutId: String,
        blockIdx: Int,
        set: Series,
    )

    suspend fun removeSeries(
        workoutId: String,
        blockIdx: Int,
        set: Series,
    )

    fun getCompletedWorkouts(): Flow<List<Workout>>

    fun getPlannedWorkouts(): Flow<List<Workout>>
}
