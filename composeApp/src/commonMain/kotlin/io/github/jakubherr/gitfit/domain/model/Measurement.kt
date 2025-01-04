package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.LocalDate

// it should be possible to track changes over time
// maybe put all daily measurements into a single document to save db reads
// a measurement needs some unit - at least metric
// if a measurement already exists for the current day, it should overwrite the old instead of adding a new one
data class Measurement(
    val id: String,
    val userId: String,
    val date: LocalDate,
    // units
    val bodyweight: Double?,
    val height: Double?,
    val neck: Double?,
    val chest: Double?,
    val leftArm: Double?,
    val leftForearm: Double?,
    val rightArm: Double?,
    val rightForearm: Double?,
    val waist: Double?,
    val leftThigh: Double?,
    val leftCalf: Double?,
    val rightThigh: Double?,
    val rightCalf: Double?,
) {
    // maybe add some simple values calculated from stored and available data
}
