package com.example.weathermaster.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weathermaster.R
import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.utils.KeyConstants
import com.example.weathermaster.utils.KeyConstants.NOTIFICATION_ID
import com.example.weathermaster.utils.LoadImage
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("ServiceCast")
@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val channelId: String = context.getString(R.string.app_name)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotificationContent(current: CityAndWeatherFormated) {
        val image = LoadImage.loadImageFromUrl(
            KeyConstants.IMAGE_URL + current.icon + KeyConstants.IMAGE_EXTENSION,
            context
        )
        notificationManager.cancel(NOTIFICATION_ID)
        val updatedNotification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.small)
            .setContentTitle(current.cityName)
            .setContentText("${current.temp}${current.tempSimbol}  ${current.description}")
            .setLargeIcon(image)
            .setOngoing(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
    }

}