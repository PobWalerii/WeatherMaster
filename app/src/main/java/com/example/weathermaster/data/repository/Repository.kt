package com.example.weathermaster.data.repository

import android.content.Context
import android.util.Log
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
    private val _pressureSimbol = MutableStateFlow("hPa")
    val pressureSimbol: StateFlow<String> = _pressureSimbol.asStateFlow()
    private val _speedSimbol = MutableStateFlow("m/s")
    val speedSimbol: StateFlow<String> = _speedSimbol.asStateFlow()
    private val _humiditySimbol = MutableStateFlow("%")
    val humiditySimbol: StateFlow<String> = _humiditySimbol.asStateFlow()

    private val latitude: StateFlow<Double> = appSettings.latitude
    private val longitude: StateFlow<Double> = appSettings.longitude
    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language
    private val measurement: StateFlow<Int> = appSettings.measurement

    private var respLatitude: Double = 0.0
    private var respLongitude: Double = 0.0

    private val _myCity = MutableStateFlow("")
    val myCity: StateFlow<String> = _myCity.asStateFlow()

    private val _currentTemp = MutableStateFlow(0.0)
    val currentTemp: StateFlow<Double> = _currentTemp.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _icon = MutableStateFlow("")
    val icon: StateFlow<String> = _icon.asStateFlow()



    fun init() {
        observeMeasurement()
        observeLocation()
        observeCityName()
    }

    private fun observeMeasurement() {
        CoroutineScope(Dispatchers.Default).launch {
            measurement.collect {
                _tempSimbol.value = listOf("K","\u2103","\u2109")[it-1]
                _speedSimbol.value = listOf("m/s","m/s","mph")[it-1]
                getWeather()
            }
        }
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
            _currentTemp.value = (response.main.temp * 10.0).roundToLong() /10.0
            _description.value = response.weather[0].description.replaceFirstChar { it.uppercase() }
            _icon.value = response.weather[0].icon
            notificationManager.updateNotificationContent(
                title = myCity.value,
                content = "${currentTemp.value}${tempSimbol.value}  ${description.value}"
            )

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun getSity(lat: Double, lon: Double) {
        try {
            val response = apiService.getCity(lat,lon,1, API_KEY)[0]
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