package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.repository.AuthRepository
import io.github.jakubherr.gitfit.domain.repository.PlanRepository
import io.github.jakubherr.gitfit.domain.repository.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.domain.repository.AuthError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// TODO handle uncached data, null value when something is not found
class FirestoreWorkoutRepository(
    private val authRepository: AuthRepository,
    private val planRepository: PlanRepository
) : WorkoutRepository {
    private val firestore = Firebase.firestore
    private val dispatcher = Dispatchers.IO
    private fun workoutRef(userId: String) = firestore.collection("USERS").document(userId).collection("WORKOUTS")

    override fun observeCurrentWorkoutOrNull(): Flow<Workout?> {
        val userId = authRepository.currentUser.id.ifBlank { return emptyFlow() }

        return workoutRef(userId)
            .where {
                ("completed" equalTo false) and ("inProgress" equalTo true)
            }
            .snapshots.map { workoutSnapshot ->
                runCatching { workoutSnapshot.documents.firstOrNull()?.data<Workout>() }.getOrNull()
            }
    }

    override suspend fun startNewWorkout(): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }

        return withContext(dispatcher) {
            val id = workoutRef(userId).document.id

            println("DBG: starting new workout with id $id")
            val workout = Workout(
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

    override suspend fun startWorkoutFromPlan(planId: String, workoutIdx: Int): Result<Unit> {
        val userId = authRepository.currentUser.id.ifBlank { return Result.failure(AuthError.UserLoggedOut) }
        println("DBG: starting planned workout with index $workoutIdx from plan $planId")

        // TODO result check
        val workoutPlan = planRepository.getCustomWorkout(userId, planId, workoutIdx)

        return withContext(dispatcher) {
            val id = workoutRef(userId).document.id

            val workout = Workout(
                id = id,
                blocks = workoutPlan.blocks,
                completed = false,
                inProgress = true,
                planId = planId,
                planWorkoutIdx = workoutIdx,
            )

            workoutRef(userId).document(id).set(workout)
            Result.success(Unit)
        }
    }

    override suspend fun completeWorkout(workoutId: String) {
        val userId = authRepository.currentUser.id.ifBlank { return}

        withContext(dispatcher) {
            println("DBG: completing workout $workoutId of user $userId")
            workoutRef(userId).document(workoutId).update("completed" to true, "inProgress" to false)
        }
    }

    override suspend fun completeWorkout(workout: Workout) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            println("DBG: completing workout ${workout.id} of user $userId")

            val newWorkout = workout.copy(completed = true, inProgress = false)
            workoutRef(userId).document(workout.id).set(newWorkout)
        }
    }

    override suspend fun deleteWorkout(workoutId: String) {
        val userId = authRepository.currentUser.id.ifBlank { return }
        withContext(dispatcher) { workoutRef(userId).document(workoutId).delete() }
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
        workoutId: String,
        exercise: Exercise,
    ) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            val workout = getWorkout(userId, workoutId)

            val block = Block(
                workout.blocks.size,
                exercise,
                emptyList(),
                null,
            )

            val newWorkout = workout.copy(blocks = workout.blocks + block)

            println("DBG: Adding new block to workout $workoutId with set")
            workoutRef(userId).document(workoutId).set(newWorkout)

        }
    }

    override suspend fun removeBlock(
        workoutId: String,
        blockIdx: Int,
    ) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            val workout = getWorkout(userId, workoutId)
            val newBlocks = workout.blocks.toMutableList()
            newBlocks.removeAt(blockIdx)
            newBlocks.forEachIndexed { idx, block ->
                newBlocks[idx] = block.copy(idx = idx)
            }
            workoutRef(userId).document(workoutId).set(workout.copy(blocks = newBlocks))
        }
    }

    override suspend fun setBlockTimer(
        workoutId: String,
        blockIdx: Int,
        seconds: Long?,
    ) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            val workout = getWorkout(userId, workoutId)
            val newBlock = workout.blocks[blockIdx].copy(restTimeSeconds = seconds)
            val newWorkout = workout.mutateBlock(newBlock)
            workoutRef(userId).document(workoutId).set(newWorkout)
        }
    }

    override suspend fun addSeries(
        workoutId: String,
        blockIdx: Int,
        set: Series,
    ) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            val workout = getWorkout(userId, workoutId)

            val oldBlock = workout.blocks[blockIdx]
            val emptySeries = Series(oldBlock.series.size, null, null, false)
            val newWorkout = workout.mutateBlock(oldBlock.copy(series = oldBlock.series + emptySeries))

            workoutRef(userId).document(workoutId).set(newWorkout)
        }
    }

    override suspend fun modifySeries(
        workoutId: String,
        blockIdx: Int,
        set: Series,
    ) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            val workout = getWorkout(userId, workoutId)
            val newBlock = workout.blocks[blockIdx].mutateSeries(set)
            val newWorkout = workout.mutateBlock(newBlock)
            workoutRef(userId).document(workoutId).set(newWorkout)
        }
    }

    private suspend fun getWorkout(
        userId: String,
        workoutId: String
    ) = workoutRef(userId).document(workoutId).get().data<Workout>()

    private fun Workout.mutateBlock(block: Block): Workout {
        val newBlocks = blocks.toMutableList()
        newBlocks[block.idx] = block
        return copy(blocks = newBlocks)
    }

    private fun Block.mutateSeries(set: Series) : Block {
        val newSeries = series.toMutableList()
        newSeries[set.idx] = set
        return copy(series = newSeries)
    }

    override suspend fun removeSeries(
        workoutId: String,
        blockIdx: Int,
        set: Series,
    ) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            val workout = getWorkout(userId, workoutId)

            val oldBlock = workout.blocks[blockIdx]
            val newBlock = oldBlock.copy(series = oldBlock.series - set)
            val newWorkout = workout.mutateBlock(newBlock)

            workoutRef(userId).document(workoutId).set(newWorkout)
        }
    }

    override fun getCompletedWorkouts(): Flow<List<Workout>> {
        val userId = authRepository.currentUser.id.ifBlank { return emptyFlow() }

        return workoutRef(userId)
            .where {
                        ("completed" equalTo true) and
                        ("inProgress" equalTo false)
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<Workout>() }
            }
    }

    override fun getPlannedWorkouts(): Flow<List<Workout>> {
        val userId = authRepository.currentUser.id.ifBlank { return emptyFlow() }

        return workoutRef(userId)
            .where {
                    ("completed" equalTo false) and
                    ("inProgress" equalTo false)
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<Workout>() }
            }
    }
}
