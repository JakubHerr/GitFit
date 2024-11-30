package io.github.jakubherr.gitfit

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.jakubherr.gitfit.di.initKoin
import io.github.jakubherr.gitfit.presentation.App

fun main() =
    application {
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
