package io.github.jakubherr.gitfit.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Block(
    val idx: Int,
    val exercise: Exercise,
    val series: List<Series> = emptyList(),
    val restTimeSeconds: Long? = null,
    val progressionSettings: ProgressionSettings? = null
) {
    fun addSeries(): Block = copy(series = series + Series(series.size, null, null, false))

    fun updateSeries(series: Series): Block {
        val newSeries = this.series.toMutableList()
        newSeries[series.idx] = series
        return copy(series = newSeries)
    }

    fun removeSeries(series: Series): Block {
        val newSeries = this.series.toMutableList()
        newSeries.remove(series)
        newSeries.forEachIndexed { idx, oldSeries ->
            newSeries[idx] = oldSeries.copy(idx = idx)
        }
        return copy(series = newSeries)
    }

    fun progressWeight(increment: Double): Block {
        val newSeries = series.toMutableList()
        newSeries.forEachIndexed { idx, series ->
            newSeries[idx] = series.copy(weight = (series.weight ?: 0.0) + increment, completed = false)
        }
        val newProgression = progressionSettings?.copy(weightThreshold = progressionSettings.weightThreshold + increment)
        return copy(series = newSeries, progressionSettings = newProgression)
    }

    fun progressReps(increment: Int): Block {
        val newSeries = series.toMutableList()
        newSeries.forEachIndexed { idx, series ->
            newSeries[idx] = series.copy(repetitions = (series.repetitions ?: 0) + increment, completed = false)
        }
        val newProgression = progressionSettings?.copy(repThreshold = progressionSettings.repThreshold + increment)
        return copy(series = newSeries, progressionSettings = newProgression)
    }
}

val mockBlock =
    Block(
        idx = 0,
        exercise = mockExercise,
        series = listOf(mockSeries),
        restTimeSeconds = 69,
    )
