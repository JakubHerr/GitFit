package io.github.jakubherr.gitfit.domain

fun String.isPositiveLong() = toLongOrNull().let { it != null && it >= 0 }

fun String.isPositiveDouble() = toDoubleOrNull().let { it != null && it >= 0.0 }

// note: this function is for UI purposes only and is double-checked by backend
fun String.isStrongPassword() = isNotBlank() && length >= 12