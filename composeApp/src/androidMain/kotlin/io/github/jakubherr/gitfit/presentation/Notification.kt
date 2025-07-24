package io.github.jakubherr.gitfit.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import io.github.jakubherr.gitfit.MainActivity
import io.github.jakubherr.gitfit.R

private val channelId = "RestNotification"

// TODO: make multiplatform once GitLive Firebase gets updated on desktop
fun Application.createNotificationChannel() {
    val channel = NotificationChannel(
        channelId,
        "Rest Notification",
        NotificationManager.IMPORTANCE_DEFAULT
    )

    val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun Application.notify() {
    val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Rest time over")
        .setContentText("Get back to work!")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    notificationManager.notify(1, notification)
}