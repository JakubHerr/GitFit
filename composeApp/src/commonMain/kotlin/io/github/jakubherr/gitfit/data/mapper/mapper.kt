package io.github.jakubherr.gitfit.data.mapper

import io.github.jakubherr.gitfit.domain.Exercise

fun toExercise(
    exerciseId: Long,
    name: String,
    description: String?,
) = Exercise(
    exerciseId,
    name,
    description,
    emptyList(),
    emptyList()
)