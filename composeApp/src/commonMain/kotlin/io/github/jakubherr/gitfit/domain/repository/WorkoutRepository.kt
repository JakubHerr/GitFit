package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeCurrentWorkoutOrNull(): Flow<Workout?>

    suspend fun startNewWorkout(): Result<Unit>

    suspend fun startWorkoutFromPlan(planId: String, workoutIdx: Int): Result<Unit>

    suspend fun completeWorkout(workoutId: String)

    suspend fun completeWorkout(workout: Workout)

    suspend fun getWorkout(workoutId: String): Result<Workout>

    suspend fun deleteWorkout(workoutId: String)

    suspend fun deleteAllWorkouts(userId: String): Result<Unit>

    suspend fun addBlock(
        workoutId: String,
        exercise: Exercise,
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
