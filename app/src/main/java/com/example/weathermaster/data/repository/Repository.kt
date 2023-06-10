package com.example.weathermaster.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.Weather
import com.example.weathermaster.data.apiservice.result.CurrentWeather
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
import kotlin.math.roundToLong

//https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&lang=ru&units=metric&appid=c5eb6539a8f80affa5ce841f35d42a90
//https://openweathermap.org/current

@Singleton
class Repository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: ApiService,
    private val appSettings: AppSettings,
    private val notificationManager: NotificationManager,
    private val applicationContext: Context,
) {

    private val _tempSimbol = MutableStateFlow("K")
    val tempSimbol: StateFlow<String> = _tempSimbol.asStateFlow()
    private val _pressureSimbol = MutableStateFlow(" hPa")
    val pressureSimbol: StateFlow<String> = _pressureSimbol.asStateFlow()
    private val _speedSimbol = MutableStateFlow("m/s")
    val speedSimbol: StateFlow<String> = _speedSimbol.asStateFlow()
    private val _humiditySimbol = MutableStateFlow(" %")
    val humiditySimbol: StateFlow<String> = _humiditySimbol.asStateFlow()

    private val latitude: StateFlow<Double> = appSettings.latitude
    private val longitude: StateFlow<Double> = appSettings.longitude
    private val currentRefresh: StateFlow<Boolean> = appSettings.currentRefresh
    private val checkCity: StateFlow<Boolean> = appSettings.checkCity
    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language
    private val measurement: StateFlow<Int> = appSettings.measurement

    private var respLatitude: Double = 0.0
    private var respLongitude: Double = 0.0

    private val _myCity = MutableStateFlow("")
    val myCity: StateFlow<String> = _myCity.asStateFlow()

    private val _currentWeather = MutableStateFlow<CurrentWeather?>(null)
    val currentWeather: StateFlow<CurrentWeather?> = _currentWeather

    fun init() {
        observeCheckCity()
        observeCurrent()
        observeMeasurement()
    }

    private fun observeCheckCity() {
        CoroutineScope(Dispatchers.Default).launch {
            checkCity.collect {
                if(it) {
                    getSity()
                }
            }
        }
    }

    private fun observeCurrent() {
        CoroutineScope(Dispatchers.Default).launch {
            combine(checkCity,currentRefresh) {checkCity,currentRefresh -> Pair(checkCity,currentRefresh)}
            .collect {(checkCity,currentRefresh) ->
                if(!checkCity && currentRefresh) {
                    getWeather()
                }
            }
        }
    }

    private fun observeMeasurement() {
        CoroutineScope(Dispatchers.Default).launch {
            measurement.collect {
                _tempSimbol.value = listOf("K","\u2103","\u2109")[it-1]
                _speedSimbol.value = listOf(" m/s"," m/s"," mph")[it-1]
                if(myCity.value.isNotEmpty()) {
                    getWeather()
                }
            }
        }
    }

    private suspend fun getWeather() {
        try {
            val response = apiService.getWeather(
                respLatitude,
                respLongitude,
                when(measurement.value) {
                    2 -> MEASUREMENT2
                    3 -> MEASUREMENT3
                    else -> MEASUREMENT1
                },
                languageCode,
                API_KEY
            )
            val current = getCurrentValues(response)
            _currentWeather.value = current

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        } finally {
            appSettings.setCurrentRefresh()
        }
    }

    private fun getCurrentValues(response: Weather): CurrentWeather {
        val main = response.main
        val weat = response.weather[0]
        val wind = response.wind
        val current =  CurrentWeather(
            ((main.temp * 10.0).roundToLong() /10.0).toString(),
            weat.description.replaceFirstChar { it.uppercase() },
            weat.icon,
            main.pressure.toString()+pressureSimbol.value,
            main.humidity.toString()+humiditySimbol.value,
            ((wind.speed * 10.0).roundToLong() /10.0).toString()+speedSimbol.value,
            ((wind.gust * 10.0).roundToLong() /10.0).toString()+speedSimbol.value
        )
        notificationManager.updateNotificationContent(
            icon = current.icon,
            title = myCity.value,
            content = "${current.temp}${tempSimbol.value}  ${current.description}"
        )
        return current
    }

    private suspend fun getSity() {
        try {
            val response = apiService.getCity(
                latitude.value,
                longitude.value,
                1, API_KEY)[0]
            val localNames = response.local_names
            val city: String? = localNames.javaClass.declaredFields
                .firstOrNull { it.name == languageCode }
                ?.let {
                    it.isAccessible = true
                    it.get(localNames) as? String
                }
            if(!city.isNullOrEmpty()) {
                _myCity.value = city
                respLatitude = response.lat
                respLongitude = response.lon
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        } finally {
            appSettings.setCheckCity()
        }
    }
}