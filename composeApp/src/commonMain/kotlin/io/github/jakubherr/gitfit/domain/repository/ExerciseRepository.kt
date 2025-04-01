package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getDefaultExercises(): Flow<List<Exercise>>

    fun getCustomExercises(userId: String): Flow<List<Exercise>>

    suspend fun addCustomExercise(userId: String, exercise: Exercise): Result<Unit>

    // TODO edit custom exercise
    // TODO delete custom exercise
    // consider what to do with all existing workouts that already use this exercise

    suspend fun getDefaultExercise(exerciseId: String): Result<Exercise>

    suspend fun getCustomExercise(userId: String, exerciseId: String): Result<Exercise>

    // TODO use to fill Firebase with predefined exercises and remove
    suspend fun addDefaultExercise(exercise: Exercise)
}
