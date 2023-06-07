package com.example.weathermaster.geolocation

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.weathermaster.R
import com.example.weathermaster.utils.KeyConstants.CHANNEL_ID
import com.example.weathermaster.utils.KeyConstants.NOTIFICATION_ID
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class LocationService(): Service() {

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var startedLocationTracking = false
    private var timeInterval: Long = 3600000
    private var minimalDistance: Float = 1000F

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations){
                val latitude = location.latitude.toString()
                val longitude = location.longitude.toString()
                showLocation(latitude,longitude)
            }
        }
    }
    private fun showLocation(latitude: String,longitude:String) {
        Toast.makeText(applicationContext,"$latitude, $longitude", Toast.LENGTH_SHORT).show()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        //val notification = setNotification(applicationContext, applicationContext.getString(CHANNEL_ID))
        //startForeground(NOTIFICATION_ID, notification)

        val channelId = applicationContext.getString(CHANNEL_ID)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Your Title")
            .setContentText("Your Content")
            .setSmallIcon(R.drawable.ico)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Your Channel", NotificationManager.IMPORTANCE_LOW)
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notification = notificationBuilder.build()
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
                setWaitForAccurateLocation(true)
            }.build()

    }


}