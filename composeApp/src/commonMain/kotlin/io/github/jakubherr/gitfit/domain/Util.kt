package io.github.jakubherr.gitfit.domain

fun String.isPositiveLong() = toLongOrNull().let { it != null && it >= 0 }

fun String.isPositiveDouble() = toDoubleOrNull().let { it != null && it >= 0.0 }
