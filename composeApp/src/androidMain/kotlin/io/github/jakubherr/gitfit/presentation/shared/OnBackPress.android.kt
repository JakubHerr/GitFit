package io.github.jakubherr.gitfit.presentation.shared

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun OnBackPress(handler: () -> Unit) {
    BackHandler { handler() }
}
