package io.github.jakubherr.gitfit.data.repository


import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.Workout
import kotlinx.coroutines.flow.flow

class FirestoreWorkoutRepository {

    private val firestore = Firebase.firestore

    fun getWorkouts() = flow {
        firestore.collection("WORKOUTS").snapshots.collect { querySnapshot ->
            val workouts = querySnapshot.documents.map { documentSnapshot ->
                documentSnapshot.data<Workout>()
            }
            emit(workouts)
        }
    }

    fun getWorkoutById(id: String) = flow {
        firestore.collection("WORKOUTS").document(id).snapshots.collect { documentSnapshot ->
            emit(documentSnapshot.data<Workout>())
        }
    }

    suspend fun addWorkout(workout: Workout) {
        println("Trying to add mock workout to db...")
        val workoutId = generateRandomStringId()
        firestore.collection("WORKOUTS")
            .document(workoutId)
            .set(workout) // TODO te
    }

//    suspend fun updateWorkout(workout: Workout) {
//        firestore.collection("WORKOUTS").document(workout.id).set(workout)
//    }
//
//    suspend fun deleteWorkout(workout: Workout) {
//        firestore.collection("WORKOUTS").document(workout.id).delete()
//    }

    private fun generateRandomStringId(length: Int = 20): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}