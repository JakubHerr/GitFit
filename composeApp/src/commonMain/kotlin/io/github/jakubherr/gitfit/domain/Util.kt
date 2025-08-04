package io.github.jakubherr.gitfit.domain

import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.ExperimentalTime

// note: these functions assume 0 IS a valid input (user failed exercise, user used no weight)
fun String.isNonNegativeLong() = toLongOrNull().let { it != null && it >= 0 }

fun Long?.isNonNegative() = this != null && this >= 0.0

fun String.isNonNegativeInt() = toIntOrNull().let { it != null && it >= 0 }

fun String.isNonNegativeDouble() = toDoubleOrNull().let { it != null && it >= 0.0 }

fun Double?.isNonNegative() = this != null && this >= 0.0

// note: these functions assume 0 is NOT a valid input (weight or rep increase in progression)
fun String.isPositiveInt() = toIntOrNull().let { it != null && it > 0 }

fun String.isPositiveDouble() = toDoubleOrNull().let { it != null && it > 0 }

fun String.validDecimals(maxDecimals: Int) = substringAfter(".", "").length <= maxDecimals

@OptIn(ExperimentalTime::class)
fun today() =
    kotlin.time.Clock.System
        .todayIn(TimeZone.currentSystemDefault())
