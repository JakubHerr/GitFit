package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Plan
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.domain.repository.AuthError
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.domain.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class FirestoreWorkoutRepository(
    private val authRepository: AuthRepository,
) : WorkoutRepository {
    private val firestore = Firebase.firestore
    private val dispatcher = Dispatchers.IO

    private fun workoutRef(userId: String) = firestore.collection("USERS").document(userId).collection("WORKOUTS")

    override fun observeCurrentWorkoutOrNull(userId: String): Flow<Workout?> {
        userId.ifBlank { return emptyFlow() }

        return workoutRef(userId)
            .where {
                ("completed" equalTo false) and ("inProgress" equalTo true)
            }
            .snapshots.map { workoutSnapshot ->
                runCatching { workoutSnapshot.documents.firstOrNull()?.data<Workout>() }.getOrNull()
            }
            .flowOn(dispatcher)
    }

    // if the device is offline and the user deletes the only completed workout, it will take longer to update for some reason
    override fun getCompletedWorkouts(userId: String): Flow<List<Workout>> {
        userId.ifBlank { return emptyFlow() }

        return workoutRef(userId)
            .where {
                ("completed" equalTo true) and ("inProgress" equalTo false)
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<Workout>() }
            }
            .flowOn(dispatcher)
    }

    override fun getPlannedWorkouts(userId: String): Flow<List<Workout>> {
        userId.ifBlank { return emptyFlow() }

        return workoutRef(userId)
            .where {
                ("completed" equalTo false) and ("inProgress" equalTo false)
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<Workout>() }
            }
            .flowOn(dispatcher)
    }

    override suspend fun startNewWorkout(): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val id = workoutRef(userId).document.id

            val workout =
                Workout(
                    id = id,
                    date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    blocks = emptyList(),
                    completed = false,
                    inProgress = true,
                )

            workoutRef(userId).document(id).set(workout)
            Result.success(Unit)
        }
    }

    override suspend fun startWorkoutFromPlan(
        plan: Plan,
        workoutIdx: Int,
    ): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val id = workoutRef(userId).document.id

            val workout =
                Workout(
                    id = id,
                    blocks = plan.workoutPlans[workoutIdx].blocks,
                    completed = false,
                    inProgress = true,
                    planId = plan.id,
                    planWorkoutIdx = workoutIdx,
                )

            runCatching { workoutRef(userId).document(id).set(workout) }
        }
    }

    override suspend fun completeWorkout(workout: Workout): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val newWorkout = workout.copy(completed = true, inProgress = false)
            runCatching { workoutRef(userId).document(workout.id).set(newWorkout) }
        }
    }

    override suspend fun deleteWorkout(workoutId: String): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }
        return withContext(dispatcher) {
            runCatching { workoutRef(userId).document(workoutId).delete() }
        }
    }

    override suspend fun deleteAllWorkouts(userId: String): Result<Unit> {
        userId.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            workoutRef(userId).get().documents.forEach { document ->
                try {
                    workoutRef(userId).document(document.id).delete()
                } catch (e: Exception) {
                    return@withContext Result.failure(e)
                }
            }
            Result.success(Unit)
        }
    }

    override suspend fun addBlock(
        workout: Workout,
        exercise: Exercise,
    ): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val newWorkout = workout.addBlock(exercise)
            runCatching { workoutRef(userId).document(workout.id).set(newWorkout) }
        }
    }

    override suspend fun removeBlock(
        workout: Workout,
        blockIdx: Int,
    ): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val newWorkout = workout.removeBlock(blockIdx)
            runCatching { workoutRef(userId).document(workout.id).set(newWorkout) }
        }
    }

    override suspend fun setBlockTimer(
        workout: Workout,
        blockIdx: Int,
        seconds: Long?,
    ): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val newWorkout = workout.updateBlock(workout.blocks[blockIdx].copy(restTimeSeconds = seconds))
            runCatching { workoutRef(userId).document(workout.id).set(newWorkout) }
        }
    }

    override suspend fun addSeries(
        workout: Workout,
        blockIdx: Int,
    ): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val newWorkout = workout.updateBlock(workout.blocks[blockIdx].addSeries())
            runCatching { workoutRef(userId).document(workout.id).set(newWorkout) }
        }
    }

    override suspend fun modifySeries(
        workout: Workout,
        blockIdx: Int,
        set: Series,
    ): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val newWorkout = workout.updateBlock(workout.blocks[blockIdx].updateSeries(set))
            runCatching { workoutRef(userId).document(workout.id).set(newWorkout) }
        }
    }

    override suspend fun removeSeries(
        workout: Workout,
        blockIdx: Int,
        set: Series,
    ): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val newWorkout = workout.updateBlock(workout.blocks[blockIdx].removeSeries(set))
            runCatching { workoutRef(userId).document(workout.id).set(newWorkout) }
        }
    }
}
