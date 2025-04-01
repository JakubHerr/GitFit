package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.repository.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    override fun getCustomExercises(userId: String): Flow<List<Exercise>> {
        if (userId.isBlank()) return emptyFlow()

        return userExerciseRef(userId).snapshots.map { snapshot ->
            snapshot.documents.map { it.data<Exercise>() }
        }
    }

    override suspend fun addCustomExercise(userId: String, exercise: Exercise) {
        withContext(context) {
            val id = userExerciseRef(userId).document.id
            userExerciseRef(userId).document(id).set(exercise.copy(id = id))
        }
    }

    override suspend fun getExerciseById(exerciseId: String): Exercise? {
        println("DBG: fetching default exercise")
        return withContext(context) {
            val result = exerciseRef.document(exerciseId).get()
            if (result.exists) result.data<Exercise>() else null
        }
    }

    override suspend fun getCustomExerciseById(userId: String, exerciseId: String): Exercise? {
        println("DBG: fetching custom exercise")
        return withContext(context) {
            val result = userExerciseRef(userId).document(exerciseId).get()
            if (result.exists) result.data<Exercise>() else null
        }
    }
}
