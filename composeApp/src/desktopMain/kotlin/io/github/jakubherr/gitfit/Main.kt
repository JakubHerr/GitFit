package io.github.jakubherr.gitfit

import android.app.Application
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import io.github.jakubherr.gitfit.data.options
import io.github.jakubherr.gitfit.di.initKoin
import io.github.jakubherr.gitfit.presentation.App

fun main() =
    application {

        // https://github.com/GitLiveApp/firebase-java-sdk?tab=readme-ov-file#initializing-the-sdk
        // TODO implement persistent storage, this only stores in memory
        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
            val storage = mutableMapOf<String, String>()
            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) = println(msg)
            override fun retrieve(key: String) = storage[key]
            override fun store(key: String, value: String) = storage.set(key, value)
        })

        Firebase.initialize(Application(), options)

        val state = rememberWindowState(
            size = DpSize(1000.dp, 800.dp),
            // position = WindowPosition(300.dp, 300.dp)
        )

        initKoin()

        Window(
            onCloseRequest = ::exitApplication,
            title = "GitFit",
            state = state,
        ) {
            App()
        }
    }
