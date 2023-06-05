package com.example.weathermaster.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.weathermaster.R
import com.example.weathermaster.data.repository.Repository
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.SplashScreen.startSplash
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appSettings: AppSettings
    @Inject
    lateinit var repository: Repository
    override fun onCreate(savedInstanceState: Bundle?) {
        startSplash(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repository.init(this)
        appSettings.init()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()
        startSplash(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        appSettings.close()
    }

}