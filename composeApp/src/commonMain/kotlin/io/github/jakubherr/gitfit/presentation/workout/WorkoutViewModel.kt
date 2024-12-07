package io.github.jakubherr.gitfit.presentation.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.jakubherr.gitfit.domain.Block
import io.github.jakubherr.gitfit.domain.Exercise
import io.github.jakubherr.gitfit.domain.Series
import io.github.jakubherr.gitfit.domain.mockWorkout

class WorkoutViewModel : ViewModel() {
    var state by mutableStateOf(mockWorkout)
        private set

    fun onAction(action: WorkoutAction) {
        when(action) {
            is WorkoutAction.AddBlock -> addBlock(action.exercise)
            is WorkoutAction.AddSet -> addSet(action.blockId, action.set)
            is WorkoutAction.ToggleSetCompletion -> toggleSetCompletion(action.setId)
        }
    }

    private fun addBlock(exercise: Exercise) {
        state = state.copy(blocks = state.blocks + Block(-1, exercise, emptyList(), null))
    }

    private fun addSet(blockId: Long, set: Series) {
        val blockListCopy = state.blocks.toMutableList()

        val block = blockListCopy.find { it.id == blockId } ?: return
        val newBlock = block.copy(series = block.series.toMutableList().apply { add(set) })
        blockListCopy[0] = newBlock
        state = state.copy(blocks = blockListCopy)
    }

    private fun toggleSetCompletion(id: Long) {
        // TODO
    }
}

sealed interface WorkoutAction {
    class AddBlock(val exercise: Exercise) : WorkoutAction
    class AddSet(val blockId: Long, val set: Series) : WorkoutAction
    class ToggleSetCompletion(val setId: Long) : WorkoutAction
}