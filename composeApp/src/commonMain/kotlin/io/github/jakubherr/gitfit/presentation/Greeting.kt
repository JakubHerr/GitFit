package io.github.jakubherr.gitfit.presentation

import io.github.jakubherr.gitfit.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}