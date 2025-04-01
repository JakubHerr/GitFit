package io.github.jakubherr.gitfit.domain.repository

import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getDefaultExercises(): Flow<List<Exercise>> // TODO use pagination to limit reads

    fun getCustomExercises(userId: String): Flow<List<Exercise>>

    suspend fun addCustomExercise(userId: String, exercise: Exercise) // TODO use results

    // TODO edit custom exercise
    // TODO delete custom exercise
    // consider what to do with all existing workouts that already use this exercise

    suspend fun getDefaultExerciseById(exerciseId: String): Result<Exercise>

    suspend fun getCustomExerciseById(userId: String, exerciseId: String): Result<Exercise>

    // TODO use to fill Firebase with predefined exercises and remove
    suspend fun addDefaultExercise(exercise: Exercise)
}
