package io.github.jakubherr.gitfit.data.repository

import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Exercise
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.WorkoutRepository

class MockWorkoutRepository : WorkoutRepository {
    private var list = mutableListOf<Workout>()

    init {

    }

    override fun addWorkout() {
        list.add(Workout(list.size.toLong(), emptyList()))
    }

    override fun addBlock(workoutId: Long, exercise: Exercise) {
        val old = list[workoutId.toInt()]
        val new = old.copy(blocks = old.blocks + Block(-1, exercise, emptyList(), null))
    }

    override fun removeBlock() {
        TODO("Not yet implemented")
    }

    override fun setBlockTimer(block: Block, seconds: Long?) {
        TODO("Not yet implemented")
    }

    override fun addEmptySeries(block: Block) {
        TODO("Not yet implemented")
    }

    override fun modifySeries(set: Series) {
        TODO("Not yet implemented")
    }
}