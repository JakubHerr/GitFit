package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.runtime.Composable

@Composable
actual fun OnBackPress(handler: () -> Unit) {
    // does nothing, desktop does not have a back button like mobile
}