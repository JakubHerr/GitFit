package io.github.jakubherr.gitfit.domain

// note: these functions assume 0 IS a valid input (user failed exercise, user used no weight)
fun String.isNonNegativeLong() = toLongOrNull().let { it != null && it >= 0 }
fun String.isNonNegativeInt() = toIntOrNull().let { it != null && it >= 0 }
fun String.isNonNegativeDouble() = toDoubleOrNull().let { it != null && it >= 0.0 }

// note: these functions assume 0 is NOT a valid input (weight or rep increase in progression)
fun String.isPositiveInt() = toIntOrNull().let { it != null && it > 0 }
fun String.isPositiveDouble() = toDoubleOrNull().let { it != null && it > 0 }

// note: this function is for UI purposes only and is double-checked by backend
fun String.isStrongPassword() = isNotBlank() && length >= 12

fun String.validDecimals(maxDecimals: Int) = substringAfter(".", "").length <= maxDecimals