package com.example.weathermaster.geolocation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weathermaster.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    private val context: Context,
) {

    private val serviceIntent = createServiceIntent()

    fun init(activity: Activity) {
        val permissionGranted = requestLocationPermission(activity)
        if (permissionGranted) {
            //if(!isRemoteService.value) {
            startService()
            //}
        }
    }

    private fun stopService() {
        //if(isRemoteService.value) {
        context.stopService(serviceIntent)
        //}
    }

    private fun startService() {

        //if(!isRemoteService.value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
            } else {
                    context.startService(serviceIntent)
            }
        //}
    }

    @SuppressLint("ObsoleteSdkInt")
    fun requestLocationPermission(activity: Activity): Boolean {
        var permissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val coarseLocationPermission =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                } else {
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                }
            val coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
                context, coarseLocationPermission
            ) == PackageManager.PERMISSION_GRANTED
            if (!coarseLocationPermissionGranted){
                val permission = arrayOf(coarseLocationPermission)
                val permissionRequested =
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])
                if (permissionRequested) {
                    Toast.makeText(activity, context.getString(R.string.permission), Toast.LENGTH_SHORT).show()
                } else {
                    ActivityCompat.requestPermissions(
                        activity,
                        permission,
                        REQUEST_LOCATION_PERMISSION
                    )
                }
            } else{
                permissionGranted = true
            }
        } else{
            permissionGranted = true
        }
        return permissionGranted
    }

    private fun createServiceIntent() = Intent(context, LocationService::class.java)

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1000
    }

}