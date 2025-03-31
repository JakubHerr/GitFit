package io.github.jakubherr.gitfit.domain.model

import kotlinx.serialization.Serializable

// this should be named Set but is named series because of name conflict with Set collection
@Serializable
data class Series(
    val idx: Int,
    val repetitions: Long?,
    val weight: Double?,
    val completed: Boolean,
) {
    val volume get() = if (weight == null || repetitions == null) null else weight * repetitions
}

val mockSeries =
    Series(
        0,
        repetitions = 3,
        weight = 40.0,
        completed = false,
    )
