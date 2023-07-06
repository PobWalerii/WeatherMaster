package com.example.weathermaster.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.weathermaster.R
import com.example.weathermaster.connectreceiver.ConnectReceiver
import com.example.weathermaster.data.repository.Repository
import com.example.weathermaster.workmanager.DataUpdateManager
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.SplashScreen.startSplash
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appSettings: AppSettings
    @Inject
    lateinit var manager: DataUpdateManager
    @Inject
    lateinit var connectReceiver: ConnectReceiver
    @Inject
    lateinit var repository: Repository


    override fun onCreate(savedInstanceState: Bundle?) {
        startSplash(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appSettings.init()
        repository.init()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        connectReceiver.init()
        manager.init(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectReceiver.close()
        appSettings.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        manager.init(this)
    }

}