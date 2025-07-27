package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable

expect @Composable fun NotificationPermissionEffect()

expect class NotificationHandler {
    fun sendNotification()
}