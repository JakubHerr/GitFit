package io.github.jakubherr.gitfit.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.jakubherr.gitfit.db.LocalDatabase
import io.github.jakubherr.gitfit.domain.Exercise
import kotlinx.coroutines.Dispatchers

class LocalExerciseDataSource(
    private val db: LocalDatabase
) : ExerciseDataSource {
    override fun getAll() = db.exerciseQueries.selectAll(
        mapper = { id, name, description ->
            Exercise(
                id,
                name,
                description,
                emptyList(),
                emptyList(),
            )
        }
    ).asFlow().mapToList(Dispatchers.IO)

    override fun insert(exercise: Exercise) {
        db.exerciseQueries.insert(exercise.name, exercise.description)
    }
}
