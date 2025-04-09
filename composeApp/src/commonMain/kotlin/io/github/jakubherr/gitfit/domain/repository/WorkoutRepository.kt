package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeCurrentWorkoutOrNull(userId: String): Flow<Workout?>

    fun getCompletedWorkouts(userId: String): Flow<List<Workout>>

    fun getPlannedWorkouts(userId: String): Flow<List<Workout>>

    suspend fun startNewWorkout(): Result<Unit>

    suspend fun startWorkoutFromPlan(
        plan: Plan,
        workoutIdx: Int,
    ): Result<Unit>

    suspend fun completeWorkout(workout: Workout)

    suspend fun deleteWorkout(workoutId: String)

    suspend fun deleteAllWorkouts(userId: String): Result<Unit>

    suspend fun addBlock(
        workout: Workout,
        exercise: Exercise,
    )

    suspend fun removeBlock(
        workout: Workout,
        blockIdx: Int,
    )

    suspend fun setBlockTimer(
        workout: Workout,
        blockIdx: Int,
        seconds: Long?,
    )

    suspend fun addSeries(
        workout: Workout,
        blockIdx: Int,
    )

    suspend fun modifySeries(
        workout: Workout,
        blockIdx: Int,
        set: Series,
    )

    suspend fun removeSeries(
        workout: Workout,
        blockIdx: Int,
        set: Series,
    )
}
