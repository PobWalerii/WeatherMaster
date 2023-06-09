package com.example.weathermaster.geolocation

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import com.example.weathermaster.notification.NotificationManager
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.KeyConstants.NOTIFICATION_ID
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class LocationService(): Service() {

    @Inject
    lateinit var appSettings: AppSettings
    @Inject
    lateinit var notificationManager: NotificationManager

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var startedLocationTracking = false
    private var timeInterval: Long = 60000
    private var minimalDistance: Float = 1000F

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations){
                appSettings.setLocation(location.latitude, location.longitude)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = notificationManager.setNotification()
        startForeground(NOTIFICATION_ID, notification)

        createLocationRequest()
        setupLocationProviderClient()
        startLocationTracking()

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        //appSettings.setIsBackService(true)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        //stopForeground(STOP_FOREGROUND_REMOVE)
        //stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        //stopForeground(STOP_FOREGROUND_REMOVE)
        //appSettings.setIsBackService(false)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    fun startLocationTracking() {
        if (!startedLocationTracking) {
            locationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
            startedLocationTracking = true
        }
    }

    fun stopLocationTracking() {
        if (startedLocationTracking) {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun setupLocationProviderClient() {
        locationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }
    private fun createLocationRequest() {
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
                setMinUpdateDistanceMeters(minimalDistance)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(false)
            }.build()
    }


}