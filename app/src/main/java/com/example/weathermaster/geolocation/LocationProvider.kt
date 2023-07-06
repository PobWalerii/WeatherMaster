package com.example.weathermaster.geolocation

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.example.weathermaster.permission.CheckPermission

class LocationProvider(private val context: Context) {

    fun getLastKnownLocation(): Location? {
        val checkPermission = CheckPermission()
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (checkPermission.checkLocationPermission(context)) {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null

            for (provider in providers) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null && (bestLocation == null || location.accuracy < bestLocation.accuracy)) {
                    bestLocation = location
                }
            }
            return bestLocation
        }
        return null
    }
}