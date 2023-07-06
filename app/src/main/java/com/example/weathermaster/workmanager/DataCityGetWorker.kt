package com.example.weathermaster.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathermaster.data.repository.RepoWeather
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataCityGetWorker  @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repoWeather: RepoWeather,
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val cityId = inputData.getLong("cityId", 0)
        return if(cityId != 0L) {
             if (repoWeather.getCityWeather(cityId)) {
                Result.success()
            } else {
                Result.retry()
            }
        } else {
            Result.failure()
        }
    }

}