package com.example.weathermaster.geolocation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import javax.inject.Inject
import javax.inject.Singleton
import com.example.weathermaster.R

@Singleton
class LocationManager_ @Inject constructor(
    private val context: Context
) {

    /*
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private var startedLocationTracking = false

    private var timeInterval: Long = 300000
    private var minimalDistance: Float = 100F

    init {
        createLocationRequest()
        setupLocationProviderClient()
    }

    @SuppressLint("ObsoleteSdkInt")
    fun requestLocationPermission(activity: Activity): Boolean {
        var permissionGranted = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val coarseLocationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION
            val coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
                context, coarseLocationPermission
            ) == PackageManager.PERMISSION_GRANTED
            if (!coarseLocationPermissionGranted){
                val permission = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
                val permissionRequested =
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])
                if (permissionRequested) {
                    Toast.makeText(activity, context.getString(R.string.permission), Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(activity, permission, REQUEST_LOCATION_PERMISSION)
                }
            } else{
                permissionGranted = true
            }
        } else{
            permissionGranted = true
        }
        return permissionGranted
    }

    private fun setupLocationProviderClient() {
        locationClient = LocationServices.getFusedLocationProviderClient(context)
    }
    private fun createLocationRequest() {
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
                setMinUpdateDistanceMeters(minimalDistance)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

    }

    @SuppressLint("MissingPermission")
    fun startLocationTracking(locationCallback: LocationCallback) {
        if (!startedLocationTracking) {
            locationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
            this.locationCallback = locationCallback
            startedLocationTracking = true
        }
    }

    fun stopLocationTracking() {
        if (startedLocationTracking) {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1000
    }

     */
}