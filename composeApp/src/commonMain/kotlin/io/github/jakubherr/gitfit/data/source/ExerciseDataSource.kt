package io.github.jakubherr.gitfit.data.source

import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseDataSource {
    fun getAll(): Flow<List<Exercise>>

    fun insert(exercise: Exercise)
}
