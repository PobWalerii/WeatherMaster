package com.example.weathermaster.workmanager

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import com.example.weathermaster.notification.NotificationService
import com.example.weathermaster.settings.AppSettings
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUpdateManager @Inject constructor(
    private val appSettings: AppSettings,
    private val applicationContext: Context,
){

    fun start(permission: Boolean) {
        appSettings.setPermission(permission)
        appSettings.setIsStarted(true)
        startDataUpdate()
        startService()
    }

    private fun startDataUpdate() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dataUpdateRequest = PeriodicWorkRequestBuilder<DataUpdateWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "data_update",
            ExistingPeriodicWorkPolicy.KEEP,
            dataUpdateRequest
        )
    }

    private fun startService() {
        val serviceIntent = createServiceIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(serviceIntent)
        } else {
            applicationContext.startService(serviceIntent)
        }
    }

    private fun createServiceIntent() = Intent(applicationContext, NotificationService::class.java)

}
