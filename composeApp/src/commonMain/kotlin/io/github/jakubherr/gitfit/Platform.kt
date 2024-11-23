package io.github.jakubherr.gitfit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform