package com.example.weathermaster.geolocation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weathermaster.R
import com.example.weathermaster.utils.KeyConstants

object ServiceNotification {

    fun setNotification(context: Context): Notification {
        val channelId = context.getString(R.string.app_name)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Title")
            .setContentText("Content")
            .setSmallIcon(R.drawable.small)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notification = notificationBuilder.build()
        return notification
    }
}