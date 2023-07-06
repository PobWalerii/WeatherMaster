package com.example.weathermaster.workmanager

import android.app.Activity
import android.content.Context
import androidx.work.*
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.permission.CheckPermission
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUpdateManager @Inject constructor(
    private val applicationContext: Context,
    private val appSettings: AppSettings
){

    private var startApp = true
    private val checkPermission = CheckPermission()

    fun init(activity: Activity) {
        val permissionGranted = checkPermission.checkLocationPermission(activity)
        if (!permissionGranted && startApp) {
            startApp = false
            checkPermission.requestLocationPermission(activity)
        } else {
            startService()
            appSettings.setIsServiceStatus()
        }
    }

    private fun startService() {

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

}
