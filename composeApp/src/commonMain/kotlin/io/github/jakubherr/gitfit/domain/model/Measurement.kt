package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

// it should be possible to track changes over time
// a measurement needs some unit - at least metric
// if a measurement already exists for the current day, it should overwrite the old instead of adding a new one
@Serializable
data class Measurement(
    val id: String,
    val userId: String,
    val date: LocalDate,
    // TODO units
    val neck: Double?,
    val chest: Double?,
    val leftArm: Double?,
    val rightArm: Double?,
    val leftForearm: Double?,
    val rightForearm: Double?,
    val waist: Double?,
    val leftThigh: Double?,
    val rightThigh: Double?,
    val leftCalf: Double?,
    val rightCalf: Double?,
    val bodyweight: Double?,
    val height: Double?,
) {
    // maybe add some simple values calculated from stored and available data
}
