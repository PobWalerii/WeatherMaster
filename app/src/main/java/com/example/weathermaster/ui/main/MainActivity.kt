package com.example.weathermaster.ui.main

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.weathermaster.R
import com.example.weathermaster.data.repository.Repository
import com.example.weathermaster.permission.CheckPermission
import com.example.weathermaster.workmanager.DataUpdateManager
import com.example.weathermaster.utils.SplashScreen.startSplash
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var manager: DataUpdateManager
    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        startSplash(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        val checkPermission = CheckPermission()
        val permissionGranted = checkPermission.checkLocationPermission(this)
        if(permissionGranted) {
            managerStart(true)
        } else {
            checkPermission.requestLocationPermission(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        managerStart(permissionGranted)
    }

    private fun managerStart(permission: Boolean) {
        manager.start(permission)
        repository.init()
    }

}