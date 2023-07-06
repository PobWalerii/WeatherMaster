package com.example.weathermaster

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import androidx.work.Configuration
import androidx.work.WorkManager
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication: Application() {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(this,
            Configuration.Builder().setWorkerFactory(hiltWorkerFactory).build())
    }

}

