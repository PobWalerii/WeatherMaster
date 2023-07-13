package com.example.weathermaster.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CheckPermission {

    @SuppressLint("ObsoleteSdkInt")
    fun checkLocationPermission(context: Context): Boolean {
        var permissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val coarseLocationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION
            val coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
                context, coarseLocationPermission
            ) == PackageManager.PERMISSION_GRANTED
            if (coarseLocationPermissionGranted) {
                permissionGranted = true
            }
        } else {
            permissionGranted = true
        }
        return permissionGranted
    }

    fun requestLocationPermission(activity: Activity) {
        val permission = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val permissionRequested =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])
        if (!permissionRequested) {
            ActivityCompat.requestPermissions(
                activity,
                permission,
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1000
    }


}