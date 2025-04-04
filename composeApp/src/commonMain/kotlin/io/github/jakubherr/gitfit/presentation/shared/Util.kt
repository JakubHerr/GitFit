package io.github.jakubherr.gitfit.presentation.shared

fun Double.toPrettyString(): String {
    return if (this % 1.0 == 0.0) this.toInt().toString() else toString()
}