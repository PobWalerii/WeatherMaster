package com.example.weathermaster.geolocation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.example.weathermaster.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    private val context: Context
) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest= LocationRequest()
    private var startedLocationTracking = false

    init {
        configureLocationRequest()
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
                requestPermissions(activity, permission, REQUEST_LOCATION_PERMISSION)
            }
            else{
                permissionGranted = true
            }
        }
        else{
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    private fun configureLocationRequest() {
        locationRequest.interval = UPDATE_INTERVAL_MILLISECONDS
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_MILLISECONDS
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun startLocationTracking(locationCallback: LocationCallback) {
        if (!startedLocationTracking) {
            //noinspection MissingPermission
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())

            this.locationCallback = locationCallback

            startedLocationTracking = true
        }
    }

    fun stopLocationTracking() {
        if (startedLocationTracking) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    companion object {
        const val UPDATE_INTERVAL_MILLISECONDS: Long = 0
        const val FASTEST_UPDATE_INTERVAL_MILLISECONDS = UPDATE_INTERVAL_MILLISECONDS / 2
        private const val REQUEST_LOCATION_PERMISSION = 1000
    }
}