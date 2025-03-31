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

    fun removeSeries(series: Series): Block = copy(series = this.series - series)
}

val mockBlock =
    Block(
        idx = 0,
        exercise = mockExercise,
        series = listOf(mockSeries),
        restTimeSeconds = 69,
    )
