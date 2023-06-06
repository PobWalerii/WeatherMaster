package com.example.weathermaster.geolocation

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.example.weathermaster.settings.AppSettings
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class LocationService @Inject constructor(
    private val appSettings: AppSettings,
    private val context: Context
): Service() {

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
        Toast.makeText(context,"$latitude, $longitude", Toast.LENGTH_SHORT).show()
    }

    fun init(activity: Activity) {
        val permissionGranted = requestLocationPermission(activity)
        if (permissionGranted) {
            startLocationTracking(locationCallback)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


}