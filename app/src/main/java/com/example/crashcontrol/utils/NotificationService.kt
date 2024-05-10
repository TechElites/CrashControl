package com.example.crashcontrol.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.crashcontrol.R

class NotificationService(private val ctx: Context, private val channelId: String) {
    private val notifications: Int = 0
    fun createNotificationChannel(name: String) {
        val channel =
            NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager: NotificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel);
    }

    fun showNotification(title: String, content: String, intent: PendingIntent) {
        val builder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(R.drawable.crash_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(ctx)) {
            if (ActivityCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(notifications, builder.build())
        }
    }
}