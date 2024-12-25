package io.github.jakubherr.gitfit.domain

fun String.isPositiveNumber() = toLongOrNull().let { it != null && it >= 0 }
