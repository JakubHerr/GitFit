package io.github.jakubherr.gitfit.domain

import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>> // TODO use pagination to limit reads

    suspend fun createExercise(exercise: Exercise) // TODO use results?
}
