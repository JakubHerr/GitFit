package io.github.jakubherr.gitfit.presentation.shared

// https://ishaileshmishra.medium.com/mastering-states-in-android-compose-b1cf8e14529d
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()

    data class Success<T>(
        val data: T,
    ) : Resource<T>()

    class Failure(
        val error: Throwable,
    ) : Resource<Nothing>()
}
