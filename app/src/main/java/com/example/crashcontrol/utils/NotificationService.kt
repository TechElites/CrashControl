package com.example.crashcontrol.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.crashcontrol.R

enum class SendNotificationResult { Granted, Disabled, Denied }

class NotificationService(private val ctx: Context) {
    private val notifications: Int = 0
    private val channelId: String = "crash"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestSendingNotification(): SendNotificationResult {
        val notificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val areNotificationEnabled = notificationManager.areNotificationsEnabled()
        if (!areNotificationEnabled) return SendNotificationResult.Disabled

        val permissionGranted = ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) return SendNotificationResult.Denied

        return SendNotificationResult.Granted
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(intent)
        }
    }

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