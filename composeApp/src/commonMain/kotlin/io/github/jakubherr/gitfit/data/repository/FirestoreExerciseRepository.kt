package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.repository.ExerciseRepository
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.repository.AuthError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FirestoreExerciseRepository : ExerciseRepository {
    private val defaultExerciseRef = Firebase.firestore.collection("EXERCISES")
    private fun userExerciseRef(userId: String) = Firebase.firestore.collection("USERS").document(userId).collection("EXERCISES")
    private val context = Dispatchers.IO

    override fun getDefaultExercises(): Flow<List<Exercise>> {
        return defaultExerciseRef.snapshots.map { snapshot ->
            snapshot.documents.mapNotNull {
                runCatching { it.data<Exercise>() }.getOrNull()
            }
        }
    }

    override fun getCustomExercises(userId: String): Flow<List<Exercise>> {
        if (userId.isBlank()) return emptyFlow()

        return userExerciseRef(userId).snapshots.map { snapshot ->
            snapshot.documents.mapNotNull {
                runCatching { it.data<Exercise>() }.getOrNull()
            }
        }
    }

    override suspend fun addCustomExercise(userId: String, exercise: Exercise): Result<Unit> {
        if (userId.isBlank()) return Result.failure(AuthError.UserLoggedOut)

        return withContext(context) {
            val id = userExerciseRef(userId).document.id
            userExerciseRef(userId).document(id).set(exercise.copy(id = id))
            Result.success(Unit)
        }
    }

    // note: editing or removing exercise will not impact existing workout records for performance and security reasons
    override suspend fun editCustomExercise(userId: String, exercise: Exercise): Result<Unit> {
        if (userId.isBlank()) return Result.failure(AuthError.UserLoggedOut)

        return withContext(context) {
            userExerciseRef(userId).document(exercise.id).set(exercise)
            Result.success(Unit)
        }
    }

    override suspend fun removeCustomExercise(userId: String, exerciseId: String): Result<Unit> {
        if (userId.isBlank()) return Result.failure(AuthError.UserLoggedOut)

        return withContext(context) {
            userExerciseRef(userId).document(exerciseId).delete()
            Result.success(Unit)
        }
    }

    override suspend fun addDefaultExercise(exercise: Exercise) {
        withContext(context) {
            val id = defaultExerciseRef.document.id
            defaultExerciseRef.document(id).set(exercise.copy(id = id))
        }
    }

    // offline-first: if id is not found in cache, it will search server -> exception
    // not finding an exercise could either mean the exercise is custom or the user is offline
    override suspend fun getDefaultExercise(exerciseId: String): Result<Exercise> {
        return withContext(context) {
            try {
                val exercise = defaultExerciseRef.document(exerciseId).get()
                Result.success(exercise.data<Exercise>())
            } catch (e: FirebaseException) {
                Result.failure(e)
            } catch (e: IllegalArgumentException) {
               Result.failure(e)
            }
        }
    }

    override suspend fun getCustomExercise(userId: String, exerciseId: String): Result<Exercise> {
        if (userId.isBlank()) return Result.failure(AuthError.UserLoggedOut)

        return withContext(context) {
            try {
                val result = userExerciseRef(userId).document(exerciseId).get()
                Result.success(result.data<Exercise>())
            } catch (e: FirebaseException) {
                Result.failure(e)
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            }
        }
    }
}
