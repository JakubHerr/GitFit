package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.Exercise
import io.github.jakubherr.gitfit.domain.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FirestoreExerciseRepository: ExerciseRepository {
    private val exerciseRef = Firebase.firestore.collection("EXERCISES")
    private val context = Dispatchers.IO

    override fun getAllExercises() = flow {
        exerciseRef.snapshots.collect { snapshot ->
            val exercises = snapshot.documents.map {
                it.data<Exercise>()
            }
            emit(exercises)
        }
    }

    override suspend fun createExercise(exercise: Exercise) {
        withContext(context)  {
            val id = exerciseRef.document.id
            exerciseRef.document(id).set(exercise.copy(id = id))
        }
    }



    // fun deleteExercise()
}