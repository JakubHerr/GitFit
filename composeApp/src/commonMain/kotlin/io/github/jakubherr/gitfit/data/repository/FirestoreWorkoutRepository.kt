package io.github.jakubherr.gitfit.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.AuthRepository
import io.github.jakubherr.gitfit.domain.PlanRepository
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// TODO handle uncached data, null value when something is not found
//  maybe store unfinished workouts locally and only upload them on completion
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
                ("completed" equalTo false) and
                        ("inProgress" equalTo true)
            }
            .snapshots.map { workoutSnapshot ->
                workoutSnapshot.documents.firstOrNull()?.data<Workout>()
            }
    }

    override suspend fun startNewWorkout() {
        withContext(dispatcher) {
            val userId = authRepository.currentUser.id.ifBlank { return@withContext } // TODO notify of failure
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
        }
    }

    override suspend fun startWorkoutFromPlan(planId: String, workoutIdx: Int) {
        val userId = authRepository.currentUser.id.ifBlank { return }
        println("DBG: starting planned workout with index $workoutIdx from plan $planId")

        val plan = planRepository.getCustomWorkout(userId, planId, workoutIdx)

        withContext(dispatcher) {
            val id = workoutRef(userId).document.id

            val workout = Workout(
                id = id,
                blocks = plan.blocks,
                completed = false,
                inProgress = true,
            )

            workoutRef(userId).document(id).set(workout)
        }
    }

    override suspend fun completeWorkout(workoutId: String) {
        val userId = authRepository.currentUser.id.ifBlank { return }
        withContext(dispatcher) {
            workoutRef(userId).document(workoutId).update("completed" to true, "inProgress" to false)
        }
    }

    override suspend fun deleteWorkout(workoutId: String) {
        val userId = authRepository.currentUser.id.ifBlank { return }
        withContext(dispatcher) { workoutRef(userId).document(workoutId).delete() }
    }

    override suspend fun addBlock(
        workoutId: String,
        exerciseId: String,
    ) {
        val userId = authRepository.currentUser.id.ifBlank { return }

        withContext(dispatcher) {
            // TODO this implementation assumes the workout only uses predefined exercises, FIX
            //  maybe just send workout and exercise as objects?
            val exercise = firestore.collection("EXERCISES").document(exerciseId).get().data<Exercise>()

            val workout = getWorkout(userId, workoutId)

            val block = Block(
                workout.blocks.size,
                exercise,
                emptyList(),
                null,
            )

            val newWorkout = workout.copy(blocks = workout.blocks + block)

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
