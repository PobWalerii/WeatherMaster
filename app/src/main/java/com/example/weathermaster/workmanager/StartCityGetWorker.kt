package com.example.weathermaster.workmanager

import android.content.Context
import androidx.work.*

object StartCityGetWorker {

    fun cityGetWeatherWorker(context: Context, cityId: Long) {

        val inputData = Data.Builder()
            .putLong("cityId", cityId)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequest.Builder(DataCityGetWorker::class.java)
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
}