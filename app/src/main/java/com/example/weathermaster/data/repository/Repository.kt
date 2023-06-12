package com.example.weathermaster.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.Current
import com.example.weathermaster.data.apiservice.response.Forecast
import com.example.weathermaster.data.apiservice.response.Weather
import com.example.weathermaster.data.apiservice.result.CurrentForecast
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
import java.text.SimpleDateFormat
import java.util.*
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

    private val _currentForecast = MutableStateFlow<List<CurrentForecast>?>(null)
    val currentForecast: StateFlow<List<CurrentForecast>?> = _currentForecast
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
                    getForecast()
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

    private suspend fun getForecast() {
        try {
            val response = apiService.getForecast(
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
            val forecast = getForecastValues(response)
            //_currentForecast.value = forecast
        } catch  (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
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
            val current: CurrentWeather = getCurrentValues(response)
            _currentWeather.value = current
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        } finally {
            appSettings.setCurrentRefresh()
        }
    }
    //https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&units=metric&lang=ru&appid=c5eb6539a8f80affa5ce841f35d42a90
    private fun getForecastValues(response: Forecast): List<CurrentForecast> {
        val featureList: MutableList<CurrentForecast> = mutableListOf()
        val list = response.list
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        var startDate = "00.00.0000"
        var minTemp = 0.0
        var maxTemp = 0.0
        val descriptionCounts = mutableMapOf<String, Int>()
        val iconCounts = mutableMapOf<String, Int>()
        Toast.makeText(applicationContext, "${list.size}", Toast.LENGTH_LONG).show()
        list.forEach {
            val main = it.main  // температура, давление, влажность
            val weat = it.weather[0]  // описание, иконка
            val dt = it.dt
            Toast.makeText(applicationContext, "Список", Toast.LENGTH_LONG).show()
            if (sdf.format(dt) != startDate) {
                if (startDate != "00.00.0000") {
                    var maxCount = 0
                    var dominantDescription = ""
                    for ((description, count) in descriptionCounts) {
                        if (count > maxCount) {
                            dominantDescription = description
                            maxCount = count
                        }
                    }
                    maxCount = 0
                    var dominantIcon = ""
                    for ((icon, count) in iconCounts) {
                        if (count > maxCount) {
                            dominantIcon = icon
                            maxCount = count
                        }
                    }
                    val index = featureList.size
                    featureList.add(
                        index,
                        CurrentForecast(
                            startDate,
                            ((minTemp * 10.0).roundToLong() / 10.0).toString() + tempSimbol.value + "/" +
                                    ((maxTemp * 10.0).roundToLong() / 10.0).toString() + tempSimbol.value + "/",
                            dominantDescription,
                            dominantIcon
                        )
                    )
                }
                minTemp = main.temp
                maxTemp = main.temp
                startDate = sdf.format(dt)
                descriptionCounts.clear()
                iconCounts.clear()
            } else {
                Toast.makeText(applicationContext, "работаем", Toast.LENGTH_LONG).show()
                if(main.temp<minTemp) minTemp = main.temp
                if(main.temp>maxTemp) maxTemp = main.temp
                var counts = iconCounts.getOrDefault(weat.icon, 0)
                iconCounts[weat.icon] = counts + 1
                counts = descriptionCounts.getOrDefault(weat.description, 0)
                descriptionCounts[weat.description] = counts + 1
            }
        }
        _currentForecast.value = featureList
        //Toast.makeText(applicationContext, "${featureList.size}", Toast.LENGTH_LONG).show()
        return featureList
    }

    private fun getCurrentValues(response: Current): CurrentWeather {
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
            val localNames = response.localNames
            var city: String? = localNames.javaClass.declaredFields
                .firstOrNull { it.name == languageCode }
                ?.let {
                    it.isAccessible = true
                    it.get(localNames) as? String
                }
            if(city.isNullOrEmpty()) {
                city = localNames.javaClass.declaredFields
                .firstOrNull { it.name == "en" }
                    ?.let {
                        it.isAccessible = true
                        it.get(localNames) as? String
                    }
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