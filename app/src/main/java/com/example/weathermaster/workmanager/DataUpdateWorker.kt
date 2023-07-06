package com.example.weathermaster.workmanager

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathermaster.data.repository.RepoCity
import com.example.weathermaster.data.repository.RepoWeather
import com.example.weathermaster.geolocation.LocationProvider
import com.example.weathermaster.permission.CheckPermission
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class DataUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repoCity: RepoCity,
    private val repoWeather: RepoWeather
): CoroutineWorker(context, params) {

    private val sPref: SharedPreferences =
        applicationContext.getSharedPreferences("MyServices", AppCompatActivity.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun doWork(): Result {
        val isLocation = updateCurrentLocation()
        val dateChanged = checkDateChange()
        val isWeather = updateCurrentWeather(dateChanged)
        return if (isLocation && isWeather) {
            if (dateChanged) {
                updateDate()
            }
            Result.success()
        } else {
            Result.retry()
        }
    }

    private fun checkDateChange(): Boolean {
        val lastDate = sPref.getString("lastDate", "0000-00-00")
        val date = dateFormat.format(Date())
        val changeDate = date != lastDate
        if(changeDate) {
            repoWeather.clearOldForecast(date)
        }
        return changeDate
    }

    private fun updateDate() {
        val date = dateFormat.format(Date())
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString("lastDate", date)
        ed.apply()
    }

    private suspend fun updateCurrentWeather(forecast: Boolean): Boolean =
        repoWeather.getWeatherAll(forecast)

    private suspend fun updateCurrentLocation(): Boolean {
        val locationProvider = LocationProvider(applicationContext)
        val location = locationProvider.getLastKnownLocation()
        val checkPermission = CheckPermission()
        return if (checkPermission.checkLocationPermission(applicationContext)) {
            if (location == null) {
                false
            } else {
                repoCity.setCurrentCity(location.latitude, location.longitude)
            }
        } else {
            true
        }
    }

}


