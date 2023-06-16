package com.example.weathermaster.data.repository

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.room.Query
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.*
import com.example.weathermaster.data.apiservice.result.CurrentForecast
import com.example.weathermaster.data.apiservice.result.CurrentWeather
import com.example.weathermaster.data.apiservice.result.SearchListItem
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.data.mapers.Mapers.getCityFromResponse
import com.example.weathermaster.data.mapers.Mapers.toCityList
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
import java.time.LocalDate
import java.time.format.TextStyle
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

    private val _isLoadData = MutableStateFlow(false)
    val isLoadData: StateFlow<Boolean> = _isLoadData.asStateFlow()

    private val _searchListItem = MutableStateFlow<List<SearchListItem>?>(null)
    val searchListItem: StateFlow<List<SearchListItem>?> = _searchListItem

    private val _addCityResult = MutableStateFlow(false)
    val addCityResult: StateFlow<Boolean> = _addCityResult

    fun init() {
        observeCheckCity()
        observeCurrent()
        observeMeasurement()
    }

    private fun observeCheckCity() {
        CoroutineScope(Dispatchers.Default).launch {
            checkCity.collect {
                if(it) {
                    getCity()
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
            //withContext(Dispatchers.Main) {
            //    val list = response.listData.size
            //    Toast.makeText(applicationContext, "$list", Toast.LENGTH_LONG).show()
            //}
            getForecastValues(response)
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

    private fun getForecastValues(response: Forecast) {
        CoroutineScope(Dispatchers.Main).launch {
            val featureList: MutableList<CurrentForecast> = mutableListOf()
            val listData = response.listData
        var startDate = "0000-00-00"
        var minTemp = 0.0
        var maxTemp = 0.0
        val descriptionCounts = mutableMapOf<String, Int>()
        val iconCounts = mutableMapOf<String, Int>()
        listData.forEach {
            val main = it.main  // температура, давление, влажность
            val weat = it.weather[0]  // описание, иконка
            val dt = it.dtTxt
            val stringDate = dt.substring(0,10)
            if (stringDate != startDate) {
                if (startDate != "0000-00-00") {
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
                            getDn(startDate).replaceFirstChar { it.uppercase() },
                            ((minTemp * 10.0).roundToLong() / 10.0).toString() + " / " +
                                    ((maxTemp * 10.0).roundToLong() / 10.0).toString() + tempSimbol.value,
                            dominantDescription.replaceFirstChar { it.uppercase() },
                            dominantIcon
                        )
                    )
                }
                minTemp = main.temp
                maxTemp = main.temp
                startDate = stringDate
                descriptionCounts.clear()
                iconCounts.clear()
            } else {
                if(main.temp<minTemp) minTemp = main.temp
                if(main.temp>maxTemp) maxTemp = main.temp
                var counts = iconCounts.getOrDefault(weat.icon, 0)
                iconCounts[weat.icon] = counts + 1
                counts = descriptionCounts.getOrDefault(weat.description, 0)
                descriptionCounts[weat.description] = counts + 1
            }
        }
        _currentForecast.value = featureList
         }
    }

    private fun getDn(startDate: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = LocalDate.parse(startDate)
            val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            return dayOfWeek
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(startDate)
            val calendar = Calendar.getInstance()
            calendar.time = date!!
            val displayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            return displayName
        }
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









    private suspend fun getCity() {
        try {
            val city: SearchListItem? = getCityFromLocation(latitude.value, longitude.value)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    applicationContext,
                    "${latitude.value} - ${city?.latitude}, ${latitude.value} - ${city?.longitude}",
                    Toast.LENGTH_LONG
                ).show()
            }

            if(city != null) {
                val cityInBaseList: List<City> =
                    weatherDao.getCityByLocation(city.latitude, city.longitude)
                val cityId = if (cityInBaseList.size == 0) {
                    weatherDao.insertCity(
                        City(
                            0,
                            0,
                            city.cityName,
                            city.latitude,
                            city.longitude,
                            city.country,
                            city.countryName,
                            city.state
                        )
                    )
                } else {
                    cityInBaseList[0].id
                }
                val cityList = weatherDao.loadCityList()
                var number = 0
                cityList.map {
                    if (it.id == cityId) {
                        if (it.number != 0) {
                            weatherDao.updateCityNumber(it.id, 0)
                        }
                    } else {
                        number++
                        if (it.number != number) {
                            weatherDao.updateCityNumber(it.id, number)
                        }
                    }
                }
                _myCity.value = city.cityName
                respLatitude = city.latitude
                respLongitude = city.longitude
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
        } finally {
            appSettings.setCheckCity()
        }
    }

    fun getSearchList(keyWord: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _isLoadData.value = true
            try {
                val response: Location = apiService.getSearchList(
                    keyWord,
                    10,
                    API_KEY
                )
                val list: List<SearchListItem> = toCityList(response, languageCode)
                _searchListItem.value = list
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show()
                }
            } finally {
                _isLoadData.value = false
            }
        }
    }

    fun searchListToNull() {
        _searchListItem.value = null
    }
    fun setAddCityResult() {
        _addCityResult.value = false
    }

    fun addCity(current: SearchListItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val city: SearchListItem? = getCityFromLocation(current.latitude, current.longitude)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    applicationContext,
                    "${respLatitude} - ${city?.latitude}, ${respLongitude} - ${city?.longitude}",
                    Toast.LENGTH_LONG
                ).show()
            }

            if(city != null) {
                if (weatherDao.getCityByLocation(city.latitude, city.longitude).size == 0) {
                    val cityList = weatherDao.loadCityList()
                    val listSize = cityList.size
                    val number =
                        if (listSize == 0) {
                            1
                        } else {
                            cityList[listSize - 1].number + 1
                        }
                    weatherDao.insertCity(
                        City(
                            0,
                            number,
                            city.cityName,
                            city.latitude,
                            city.longitude,
                            city.country,
                            city.countryName,
                            city.state
                        )
                    )
                    _addCityResult.value = true
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            "Такой город уже внесен в список",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private suspend fun getCityFromLocation(latitude: Double, longitude: Double): SearchListItem? {
        try {
            val response: LocationItem = apiService.getCity(
                latitude,
                longitude,
                1, API_KEY
            )[0]
            val city: SearchListItem = getCityFromResponse(response, languageCode)
            return city
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            return null
        }
    }

    suspend fun loadCityList() = weatherDao.loadCityList()


}