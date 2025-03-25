package io.github.jakubherr.gitfit.domain

import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getDefaultExercises(): Flow<List<Exercise>> // TODO use pagination to limit reads

    fun getCustomExercises(userId: String): Flow<List<Exercise>>

    suspend fun addCustomExercise(userId: String, exercise: Exercise) // TODO use results?

    // TODO use to fill Firebase with predefined exercises and remove
    suspend fun addDefaultExercise(exercise: Exercise)
}
