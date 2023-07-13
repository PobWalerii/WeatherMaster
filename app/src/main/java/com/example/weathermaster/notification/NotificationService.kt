package com.example.weathermaster.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.weathermaster.data.repository.Repository
import com.example.weathermaster.utils.KeyConstants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class NotificationService: Service() {
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var repository: Repository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = notificationManager.setNotification()
        startForeground(NOTIFICATION_ID, notification)

        observeCurrentDataChanged()

        return START_STICKY
    }

    private fun observeCurrentDataChanged() {
        CoroutineScope(Dispatchers.Default).launch {
            repository.currentData.distinctUntilChanged().collect { data ->
                notificationManager.updateNotificationContent(data)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}