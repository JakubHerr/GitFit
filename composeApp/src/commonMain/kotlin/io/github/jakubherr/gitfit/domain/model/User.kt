package io.github.jakubherr.gitfit.domain.model

data class User(
    val id: String,
    val email: String,
    val loggedIn: Boolean,
    val emailVerified: Boolean,
) {
    companion object {
        val LoggedOut = User("","", false, false)
    }
}
