package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseException
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

    // offline-first: if id is not found in cache, it will search server -> exception
    // not finding an exercise could either mean the exercise is custom or the user is offline
    override suspend fun getDefaultExerciseById(exerciseId: String): Result<Exercise> {
        println("DBG: fetching default exercise")
        return withContext(context) {
            try {
                val exercise = exerciseRef.document(exerciseId).get()
                return@withContext Result.success(exercise.data<Exercise>())
            } catch (e: FirebaseException) {
                if (e.message?.contains("offline") == true) {
                    println("DBG: client is offline!")
                }
                println("DBG: exercise fetch failed, ${e.stackTraceToString()}")
                return@withContext Result.failure(e)
            }
        }
    }

    override suspend fun getCustomExerciseById(userId: String, exerciseId: String): Result<Exercise> {
        println("DBG: fetching custom exercise")
        return withContext(context) {
            try {
                val result = userExerciseRef(userId).document(exerciseId).get()
                return@withContext Result.success(result.data<Exercise>())
            } catch (e: FirebaseException) {
                return@withContext Result.failure(e)
            }
        }
    }
}
