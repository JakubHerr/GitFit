package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getDefaultExercises(): Flow<List<Exercise>>

    fun getCustomExercises(userId: String): Flow<List<Exercise>>

    suspend fun addCustomExercise(
        userId: String,
        exercise: Exercise,
    ): Result<Unit>

    suspend fun editCustomExercise(
        userId: String,
        exercise: Exercise,
    ): Result<Unit>

    suspend fun removeCustomExercise(
        userId: String,
        exerciseId: String,
    ): Result<Unit>

    suspend fun removeAllCustomExercises(userId: String): Result<Unit>

    // TODO use to fill Firebase with predefined exercises and remove
    suspend fun addDefaultExercise(exercise: Exercise)
}
