package com.example.weathermaster.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathermaster.data.repository.RepoCity
import com.example.weathermaster.data.repository.RepoWeather
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CityGetCurrentWorker  @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repoCity: RepoCity,
    private val repoWeather: RepoWeather,
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val isLocation = repoCity.updateCurrentLocation()
        return if (isLocation) {
            if (repoWeather.getCityWeather(1L)) {
                Result.success()
            } else {
                Result.retry()
            }
        } else {
            Result.retry()
        }
    }

}