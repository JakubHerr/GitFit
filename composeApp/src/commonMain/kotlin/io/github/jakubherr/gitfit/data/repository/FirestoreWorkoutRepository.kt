package io.github.jakubherr.gitfit.data.repository


import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.model.Block
import io.github.jakubherr.gitfit.domain.model.Exercise
import io.github.jakubherr.gitfit.domain.model.Series
import io.github.jakubherr.gitfit.domain.model.Workout
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable

@Serializable
private data class WorkoutDTO(
    val id: String,
    val date: LocalDate,
    val completed: Boolean = false,
    val inProgress: Boolean = false,
)

// TODO handle uncached data, null value when something is not found
//  maybe store unfinished workouts locally and only upload them on completion
class FirestoreWorkoutRepository : WorkoutRepository {
    private val firestore = Firebase.firestore
    private val dispatcher = Dispatchers.IO
    private val workoutRef = firestore.collection("WORKOUTS")

    private fun blockRef(workoutId: String, blockId: String) = workoutRef.document(workoutId).collection("BLOCKS").document(blockId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentWorkoutOrNull() = observeCurrentWorkout()
        .flatMapLatest { workoutDto ->
            workoutDto?.let {
                observeBlocks(workoutDto.id).map { blocks ->
                    Workout(
                        it.id,
                        blocks,
                        it.date,
                        it.completed
                    )
                }
            } ?: flowOf(null)
        }

    override suspend fun startNewWorkout() {
        withContext(dispatcher) {
            val id = workoutRef.document.id
            val workout = WorkoutDTO(
                id = id,
                date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                completed = false,
                inProgress = true
            )

            workoutRef.document(id).set(workout)
        }
    }

    override suspend fun startPlannedWorkout(workoutId: String) {
        withContext(dispatcher) {
            workoutRef.document(workoutId).update("inProgress" to true)
        }
    }

    override suspend fun completeWorkout(workoutId: String) {
        withContext(dispatcher) {
            workoutRef.document(workoutId).update("completed" to true, "inProgress" to false)
        }
    }

    override suspend fun deleteWorkout(workoutId: String) {
        withContext(dispatcher) {
            firestore.runTransaction {
                val blocks = workoutRef.document(workoutId).collection("BLOCKS").get().documents
                blocks.forEach {
                    launch {
                        workoutRef.document(workoutId).collection("BLOCKS").document(it.id).delete()
                    }
                }
                workoutRef.document(workoutId).delete()
            }
        }
    }

    override suspend fun addBlock(workoutId: String, exerciseId: String) {
        withContext(dispatcher) {
            val exercise = firestore.collection("EXERCISES").document(exerciseId).get().data<Exercise>()

            val id = workoutRef.document(workoutId).collection("BLOCKS").document.id
            val block = Block(
                id,
                exercise,
                emptyList(),
                null
            )

            blockRef(workoutId, id).set(block)
        }
    }

    override suspend fun removeBlock(workoutId: String, blockId: String) {
        withContext(dispatcher) {
            blockRef(workoutId, blockId).delete()
        }
    }

    override suspend fun setBlockTimer(workoutId: String, blockId: String, seconds: Long?) {
        withContext(dispatcher) {
            blockRef(workoutId, blockId).update("restTimeSeconds" to seconds)
        }
    }

    override suspend fun addSeries(workoutId: String, blockId: String, set: Series) {
        withContext(dispatcher) {
            blockRef(workoutId, blockId).update("series" to FieldValue.arrayUnion(set))
        }
    }

    override suspend fun modifySeries(workoutId: String, blockId: String, set: Series) {
        val field = blockRef(workoutId, blockId).get().data<Block>().series
        val newField = field.map { if (it.id == set.id) set else it }

        blockRef(workoutId, blockId).update("series" to newField)
    }

    override suspend fun removeSeries(workoutId: String, blockId: String, set: Series) {
        withContext(dispatcher) {
            blockRef(workoutId, blockId).update("series" to FieldValue.arrayRemove(set))
        }
    }

    override suspend fun getCompletedWorkouts() {
        TODO("Not yet implemented")
    }

    override suspend fun getPlannedWorkouts(): List<Workout> {
        return workoutRef
            .where {
                "completed" equalTo false
                "inProgress" equalTo true
            }
            .get()
            .documents
            .map { it.data<Workout>() }
    }

    private fun observeBlocks(workoutId: String) = workoutRef
        .document(workoutId)
        .collection("BLOCKS")
        .snapshots.map { querySnapshot ->
            querySnapshot.documents.map { it.data<Block>() }
        }

    private fun observeCurrentWorkout() = workoutRef
        .where {
            "completed" equalTo false
            "inProgress" equalTo true
        }
        .snapshots.map { workoutSnapshot ->
            val workoutDto = workoutSnapshot.documents.firstOrNull()?.data<WorkoutDTO>()
            workoutDto
        }
}
