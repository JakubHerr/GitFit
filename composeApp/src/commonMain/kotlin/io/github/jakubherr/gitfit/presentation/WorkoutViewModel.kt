package io.github.jakubherr.gitfit.presentation

import androidx.lifecycle.ViewModel
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Workout
import io.github.jakubherr.gitfit.domain.mockExercise
import io.github.jakubherr.gitfit.domain.mockSeries
import io.github.jakubherr.gitfit.domain.mockWorkout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkoutViewModel : ViewModel() {
    private val _state = MutableStateFlow(mockWorkout)
    val state = _state.asStateFlow()

    fun addBlock(workout: Workout): Workout {
        return workout.copy(blocks = workout.blocks + Block(-1, mockExercise, emptyList(), null))
    }

    fun addSet(block: Block): Block {
        val list = block.series.toMutableList()
        list.add(mockSeries)
        return block.copy(series = list)
    }
}