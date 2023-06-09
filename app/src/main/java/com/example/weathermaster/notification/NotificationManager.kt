package com.example.weathermaster.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weathermaster.R
import com.example.weathermaster.utils.KeyConstants.IMAGE_EXTENSION
import com.example.weathermaster.utils.KeyConstants.IMAGE_URL
import com.example.weathermaster.utils.KeyConstants.NOTIFICATION_ID
import javax.inject.Inject
import javax.inject.Singleton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

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

    fun setNotification(): Notification {
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.small)
            //.setContentTitle("")
            //.setContentText("")

        val notification = notificationBuilder.build()
        return notification
    }

    fun updateNotificationContent(icon: String = "", title: String = "", content: String = "") {
        var image: Bitmap? = null
        loadImageFromUrl(IMAGE_URL + icon + IMAGE_EXTENSION) {
            bitmap ->  image = bitmap
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.small)

        if(image != null) {
            notificationBuilder.setLargeIcon(image)
        }

        if(title.isNotEmpty()) {
            notificationBuilder.setContentTitle(title)
        }
        if(content.isNotEmpty()) {
            notificationBuilder.setContentText(content)
        }
        val updatedNotification = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
    }

    fun loadImageFromUrl(imageUrl: String, callback: (Bitmap?) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback(resource)
                }
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    callback(null)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }





}