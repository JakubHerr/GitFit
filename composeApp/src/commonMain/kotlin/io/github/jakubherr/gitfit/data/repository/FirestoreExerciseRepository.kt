package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FirestoreExerciseRepository : ExerciseRepository {
    private val exerciseRef = Firebase.firestore.collection("EXERCISES")
    private fun userExerciseRef(userId: String) = Firebase.firestore.collection("USERS").document(userId).collection("EXERCISES")
    private val context = Dispatchers.IO

    override fun getDefaultExercises() =
        flow {
            exerciseRef.snapshots.collect { snapshot ->
                val exercises =
                    snapshot.documents.map {
                        it.data<Exercise>()
                    }
                emit(exercises)
            }
        }

    override suspend fun addDefaultExercise(exercise: Exercise) {
        withContext(context) {
            val id = exerciseRef.document.id
            exerciseRef.document(id).set(exercise.copy(id = id))
        }
    }

    override fun getCustomExercises(userId: String): Flow<List<Exercise>> = flow {
        userExerciseRef(userId).snapshots.collect { snapshot ->
            val exercises = snapshot.documents.map { it.data<Exercise>() }
            emit(exercises)
        }
    }

    override suspend fun addCustomExercise(userId: String, exercise: Exercise) {
        withContext(context) {
            val id = userExerciseRef(userId).document.id
            userExerciseRef(userId).document(id).set(exercise.copy(id = id))
        }
    }

    // consider what to do with all existing workouts that already use this exercise
    // fun deleteCustomExercise()
}
