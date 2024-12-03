package io.github.jakubherr.gitfit.domain

// should be able to add a block with exercise, add a set, add weight and reps to set, complete set, add timer to block
interface WorkoutRepository {
    fun addWorkout()
    fun addBlock(workoutId: Long, exercise: Exercise)
    fun removeBlock()
    fun setBlockTimer(block: Block, seconds: Long?)

    fun addEmptySeries(block: Block) // maybe not necessary, just add series when weight and/or reps are added?
    fun modifySeries(set: Series)
}