package io.github.jakubherr.gitfit.data.repository


import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Exercise
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flow
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
class FirestoreWorkoutRepository: WorkoutRepository {
    // maybe store unfinished workouts locally and only upload them on completion
    private val firestore = Firebase.firestore
    private val workoutRef = firestore.collection("WORKOUTS")

    fun observeBlocks(workoutId: String) = flow {
        workoutRef
            .document(workoutId)
            .collection("BLOCKS")
            .snapshots.collect { querySnapshot ->
                val blocks = querySnapshot.documents.map { it.data<Block>() }
                emit(blocks)
            }
    }

    private suspend fun getBlocks(workoutId: String) = workoutRef
        .document(workoutId)
        .collection("BLOCKS")
        .get()
        .documents
        .map { it.data<Block>()}

    override suspend fun startNewWorkout() {
        println("Trying to start new workout..")
        val id = workoutRef.document.id
        val workout = WorkoutDTO(
            id = id,
            date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            completed = false,
            inProgress = true
        )

        workoutRef
            .document(id)
            .set(workout) // set basically acts as upsert
    }

    override suspend fun startPlannedWorkout(workoutId: String) {
        workoutRef.document(workoutId).update("inProgress" to true)
    }

    override suspend fun completeWorkout(workoutId: String) {
        workoutRef.document(workoutId).update("completed" to true, "inProgress" to false)
    }

    override suspend fun deleteWorkout(workoutId: String) {
        // TODO optimize, maybe try transaction?
        val blocks = workoutRef.document(workoutId).collection("BLOCKS").get().documents
        blocks.forEach {
            withContext(Dispatchers.IO) {
                launch { workoutRef.document(workoutId).collection("BLOCKS").document(it.id).delete() }
            }
        }
        workoutRef.document(workoutId).delete()
    }

    private suspend fun getCurrentWorkoutOrNull(): Workout? {
        val workout = workoutRef
            .where { "completed" equalTo false }
            .get().documents.firstOrNull()?.data<WorkoutDTO>()
        return workout?.let {
            val blocks = getBlocks(it.id)
            Workout(
                it.id,
                blocks,
                it.date,
                it.completed
            )
        }
    }

    override suspend fun addBlock(workoutId: String, exerciseId: String) {
        val exercise = firestore.collection("EXERCISES").document(exerciseId).get().data<Exercise>()

        val id = workoutRef.document(workoutId).collection("BLOCKS").document.id
        val block = Block(
            id,
            exercise,
            emptyList(),
            null
        )

        workoutRef.document(workoutId).collection("BLOCKS").document(id).set(block)
    }

    override suspend fun removeBlock(workoutId: String, blockId: String) {
        workoutRef.document(workoutId).collection("BLOCKS").document(blockId).delete()
    }

    override suspend fun setBlockTimer(blockId: String, seconds: Long?) {
        TODO("Not yet implemented")
    }

    override suspend fun addSeries(workoutId: String, blockId: String, set: Series) {
        // TODO try to add directly without fetching first
        val blockRef = workoutRef.document(workoutId).collection("BLOCKS").document(blockId)
        val test = blockRef.get()
        val block = test.data<Block>()
        val newBlock = block.copy(series = block.series + set)
        blockRef.set(newBlock)
    }

    override suspend fun toggleSeries(seriesId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getCompletedWorkouts() {
        TODO("Not yet implemented")
    }

    override suspend fun getPlannedWorkouts() {
        TODO("Not yet implemented")
    }

    override suspend fun modifySeries(set: Series) {
        TODO("Not yet implemented")
    }

    override fun observeCurrentWorkoutOrNull() = flow {
        workoutRef
            .where {
                "completed" equalTo false
                "inProgress" equalTo true
            }
            .snapshots.collect { querySnapshot ->
                val workoutDto = querySnapshot.documents.firstOrNull()?.data<WorkoutDTO>()
                val workout = workoutDto?.let {
                    Workout(
                        it.id,
                        getBlocks(it.id),
                        it.date,
                        it.completed
                    )
                }
                emit(workout)
            }
    }
}