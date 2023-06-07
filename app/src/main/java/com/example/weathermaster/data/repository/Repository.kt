package com.example.weathermaster.data.repository

import android.app.Activity
import android.content.Context

import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.geolocation.LocationManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val weatherDao: WeatherDao,
    //private val apiService: ApiService,
    private val locationManager: LocationManager,
    private val applicationContext: Context,
) {

    /*
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
        Toast.makeText(applicationContext,"$latitude, $longitude",Toast.LENGTH_SHORT).show()
    }

    fun init(activity: Activity) {
        val permissionGranted = locationManager.requestLocationPermission(activity)
        if (permissionGranted) {
            locationManager.startLocationTracking(locationCallback)
        }
    }

     */

}