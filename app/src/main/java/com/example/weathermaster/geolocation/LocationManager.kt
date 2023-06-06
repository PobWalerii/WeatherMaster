package com.example.weathermaster.geolocation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.example.weathermaster.R
import com.google.android.gms.location.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    private val context: Context
): LocationCallback() {
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    var locationPermissionGranted = false
    private var startedLocationTracking = false

    private var timeInterval: Long = 1000
    private var minimalDistance: Float = 1F

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
                    showAlert("Location permission is required for the app to function properly.")
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

    fun showAlert(message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok_button_title, null)
        val dialog = builder.create()
        dialog.show()
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
}