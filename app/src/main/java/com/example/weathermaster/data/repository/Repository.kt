package com.example.weathermaster.data.repository

import android.content.Context
import android.widget.Toast
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.notification.NotificationManager
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.KeyConstants.API_KEY
import com.example.weathermaster.utils.KeyConstants.MEASUREMENT1
import com.example.weathermaster.utils.KeyConstants.MEASUREMENT2
import com.example.weathermaster.utils.KeyConstants.MEASUREMENT3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: ApiService,
    private val appSettings: AppSettings,
    private val notificationManager: NotificationManager,
    private val applicationContext: Context,
) {

    private val latitude: StateFlow<Double> = appSettings.latitude
    private val longitude: StateFlow<Double> = appSettings.longitude
    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language
    private val measurement: StateFlow<Int> = appSettings.measurement

    private val _myCity = MutableStateFlow("")
    val myCity: StateFlow<String> = _myCity.asStateFlow()

    fun init() {
        observeLocation()
        observeCityName()
    }

    private fun observeLocation() {
        CoroutineScope(Dispatchers.Default).launch {
            combine(
                latitude,
                longitude
            ) { lat, lon -> Pair(lat, lon) }.collect { (lat, lon) ->
                if(lat != 0.0 || lon != 0.0) {
                    getSity(lat, lon)
                }
            }
        }
    }

    private suspend fun getWeather() {
        try {
            val response = apiService.getWeather(
                latitude.value,
                longitude.value,
                when(measurement.value) {
                    2 -> MEASUREMENT2
                    3 -> MEASUREMENT3
                    else -> MEASUREMENT1
                },
                languageCode,
                API_KEY
            )




        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        }










    }

    private suspend fun getSity(lat: Double, lon: Double) {
        try {
            val response = apiService.getCity(lat,lon,1, API_KEY)
            val localNames = response[0].local_names
            val city: String? = localNames.javaClass.declaredFields
                .firstOrNull { it.name == languageCode }
                ?.let {
                    it.isAccessible = true
                    it.get(localNames) as? String
                }
            if(city != null) {
                _myCity.value = city
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeCityName() {
        CoroutineScope(Dispatchers.Default).launch {
            myCity.collect {
                notificationManager.updateNotificationContent(title = it, "Ждем данные...")
                getWeather()
            }
        }



    }







}