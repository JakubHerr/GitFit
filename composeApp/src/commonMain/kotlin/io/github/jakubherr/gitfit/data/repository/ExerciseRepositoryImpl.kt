package io.github.jakubherr.gitfit.data.repository

import io.github.jakubherr.gitfit.data.source.ExerciseDataSource
import io.github.jakubherr.gitfit.domain.Exercise
import io.github.jakubherr.gitfit.domain.ExerciseRepository
import kotlinx.coroutines.flow.Flow

class ExerciseRepositoryImpl(
    private val local: ExerciseDataSource
) : ExerciseRepository {
    override fun getAllExercises(): Flow<List<Exercise>> {
        return local.getAll()
    }

    override fun createExercise(exercise: Exercise) {
        local.insert(exercise)
    }
}
