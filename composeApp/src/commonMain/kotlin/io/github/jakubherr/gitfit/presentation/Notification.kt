package io.github.jakubherr.gitfit.presentation

import androidx.compose.runtime.Composable

@Composable expect fun NotificationPermissionEffect()

expect class NotificationHandler {
    fun sendNotification()
}
