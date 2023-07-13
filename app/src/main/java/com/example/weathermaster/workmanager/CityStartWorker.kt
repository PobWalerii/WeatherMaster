package com.example.weathermaster.workmanager

import android.content.Context
import androidx.work.*

object CityStartWorker {

    fun cityGetWeatherWorker(context: Context, cityId: Long) {

        val inputData = Data.Builder()
            .putLong("cityId", cityId)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequest.Builder(CityGetDataWorker::class.java)
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "city_${cityId}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }


    fun getCurrentCityWorker(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(CityGetCurrentWorker::class.java)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "current_city",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

    }


}