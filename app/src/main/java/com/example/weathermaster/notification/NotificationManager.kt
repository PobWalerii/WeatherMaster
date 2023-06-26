package com.example.weathermaster.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.weathermaster.R
import com.example.weathermaster.utils.KeyConstants.IMAGE_EXTENSION
import com.example.weathermaster.utils.KeyConstants.IMAGE_URL
import com.example.weathermaster.utils.KeyConstants.NOTIFICATION_ID
import javax.inject.Inject
import javax.inject.Singleton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@SuppressLint("ServiceCast")
@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {

    //private val _isNotification = MutableStateFlow(false)
    //val isNotification: StateFlow<Boolean> = _isNotification.asStateFlow()

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

    fun setNotification(title: String = "", content: String = ""): Notification {
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.small)
            .setContentTitle(title)
            .setContentText(content)
        //_isNotification.value = true
        return notificationBuilder.build()
    }


    fun updateNotificationContent(title: String, content: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, title + "  " + content, Toast.LENGTH_SHORT).show()
        }
        notificationManager.cancel(NOTIFICATION_ID)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.small)
            .setContentTitle(title)
            .setContentText(content)
        val updatedNotification = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
    }

}