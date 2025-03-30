package io.github.jakubherr.gitfit.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

// for now, all measurements are assumed to be in cm and kg
@Serializable
data class Measurement(
    val date: LocalDate,
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
    val isValid: Boolean
        get() {
            val list = listOf(
                neck,
                chest,
                leftArm,
                rightArm,
                leftForearm,
                rightForearm,
                waist,
                leftThigh,
                rightThigh,
                leftCalf,
                rightCalf,
                bodyweight,
                height
            )
            return list.all { it != null && it >= 0.0 }
        }
}
