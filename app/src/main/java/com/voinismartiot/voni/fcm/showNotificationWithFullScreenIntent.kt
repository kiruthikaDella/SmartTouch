package com.voinismartiot.voni.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.voinismartiot.voni.AppDelegate
import com.voinismartiot.voni.R

fun Context.showNotificationWithFullScreenIntent(
    channelId: String = CHANNEL_ID,
    title: String = "",
    description: String = ""
) {

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(description)
        .setAutoCancel(true)
        .setVibrate(longArrayOf(1000, 1000))
        .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = AppDelegate.instance.getString(R.string.app_name)

        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION).build()

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            enableLights(true)
            enableVibration(true)
            setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, attribute)
            setImportance(importance)
        }

        notificationManager.createNotificationChannel(channel)
    }

    val notification = builder.build()
    notification.flags = Notification.FLAG_INSISTENT
    notificationManager.notify(101, notification)
}

private const val CHANNEL_ID = "channelId"