package com.example.weathermaster.data.repository

import android.content.Context
import android.widget.Toast
import com.example.weathermaster.R
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.*
import com.example.weathermaster.data.apiservice.result.SearchListItem
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.*
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.data.mapers.Mapers.forecastResponseToForecastWeather
import com.example.weathermaster.data.mapers.Mapers.forecastToForecastWeatherDay
import com.example.weathermaster.data.mapers.Mapers.getCityFromResponse
import com.example.weathermaster.data.mapers.Mapers.toCityList
import com.example.weathermaster.data.mapers.Mapers.weaterResponseToCurrentWeather
import com.example.weathermaster.data.mapers.Mapers.weaterToWeaterFormated
import com.example.weathermaster.notification.NotificationManager
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.KeyConstants.API_KEY
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

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

    private val measurement: StateFlow<Int> = appSettings.measurement
    private val latitude: StateFlow<Double> = appSettings.latitude
    private val longitude: StateFlow<Double> = appSettings.longitude

    private val checkCity: StateFlow<Boolean> = appSettings.checkCity

    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language
    private val isConnectStatus: StateFlow<Boolean> = appSettings.isConnectStatus
    private val isPermissionStatus: StateFlow<Boolean> = appSettings.isPermissionStatus

    //private val isNotification: StateFlow<Boolean> = notificationManager.isNotification

    val listCityAndWeather: Flow<List<CityAndWeatherFormated>> = weatherDao.getCityAndWeatherList()
        .map { list ->
            list.map {
                weaterToWeaterFormated(it, measurement.value)
            }
        }

    val listCityForecastDay: Flow<List<ForecastWeatherDay>> = weatherDao.getForecastList()
        .map { list ->
            forecastToForecastWeatherDay(list, measurement.value)
        }

    private val _necessaryLoadCurrent = MutableStateFlow(false)
    private val necessaryLoadCurrent: StateFlow<Boolean> = _necessaryLoadCurrent

    private val _necessaryLoadAll = MutableStateFlow(false)
    private val necessaryLoadAll: StateFlow<Boolean> = _necessaryLoadAll

    private val _isLoadData = MutableStateFlow(false)
    val isLoadData: StateFlow<Boolean> = _isLoadData.asStateFlow()

    private val _searchListItem = MutableStateFlow<List<SearchListItem>?>(null)
    val searchListItem: StateFlow<List<SearchListItem>?> = _searchListItem

    private val _addCityResult = MutableStateFlow(false)
    val addCityResult: StateFlow<Boolean> = _addCityResult

    private val _cityAddId = MutableStateFlow(0L)
    val cityAddId: StateFlow<Long> = _cityAddId

    fun init() {
        showNotification()
        observePermission()
        reservationMyCity()
        observeAddCity()
    }


    private fun showNotification() {
        CoroutineScope(Dispatchers.Default).launch {
            listCityAndWeather.collect { list ->
                if (list.isNotEmpty()) {
                    val current = list[0]
                    if (current.number == 0) {
                        notificationManager.updateNotificationContent(
                            current.cityName,
                            "${current.temp}${current.tempSimbol}  ${current.description}"
                        )
                    }
                }
            }
        }
    }

/*
    private fun showNotification() {
        CoroutineScope(Dispatchers.Default).launch {
            combine(listCityAndWeather, isNotification) { list, notifi ->
                Pair(list, notifi)
            }.collect { (list, notifi) ->
                if (list.isNotEmpty()) {
                    val current = list[0]
                    if (current.number == 0) {
                        notificationManager.updateNotificationContent(
                            current.cityName,
                            "${current.temp}${current.tempSimbol}  ${current.description}"
                        )
                    }
                }
            }
        }
    }

 */


    private fun observePermission() {
        CoroutineScope(Dispatchers.Default).launch {
            isPermissionStatus.collect {
                if(it) {
                    observeCheckCity()
                }
            }
        }
    }

    private fun observeCheckCity() {
        CoroutineScope(Dispatchers.Default).launch {
            combine(
                checkCity,
                isConnectStatus,
            ) { checkCity, isConnect ->
                checkCity && isConnect
            }.collect {
                if (it) {
                    getCity()
                }
            }
        }
    }



    private suspend fun getForecast(city: City): Boolean {
        try {
            val response = apiService.getForecast(
                city.latitude,
                city.longitude,
                languageCode,
                API_KEY
            )
            val forecast: List<ForecastWeather> = forecastResponseToForecastWeather(response, city.id)
            weatherDao.updateForecast(forecast)
            return true
        } catch  (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            return false
        }
    }

    private suspend fun getWeatherAll() {
        weatherDao.loadCityList().filter { it.number != 0 }.map {
            getWeather(it)
        }
    }

// Запрос текущей погоды
    private suspend fun getWeather(city: City): Boolean {
        try {
            val response: Current = apiService.getWeather(
                city.latitude,
                city.longitude,
                languageCode,
                API_KEY
            )
            val current: CurrentWeather = weaterResponseToCurrentWeather(response, city.id)
            weatherDao.insertCurrentWeather(current)
            return true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            return false
        }
    }



    private suspend fun getCity() {
        try {
            val city: SearchListItem? = getCityFromLocation(latitude.value, longitude.value)
            if (city != null) {
                val currentCity = City(
                    1,
                    0,
                    city.cityName,
                    city.latitude,
                    city.longitude,
                    city.country,
                    city.countryName,
                    city.state
                )
                weatherDao.insertCity( currentCity )
                if ( getWeather(currentCity) ) {
                    if( !getForecast(currentCity) ) {
                        _necessaryLoadCurrent.value = true
                    }
                } else {
                    _necessaryLoadCurrent.value = true
                }
                appSettings.setCheckCity()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
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
            if(city != null) {
                val currentCity = weatherDao.getCityByLocation(city.latitude, city.longitude)
                if (currentCity.isEmpty()) {
                    val cityList = weatherDao.loadCityList()
                    val listSize = cityList.size
                    val number =
                        if (listSize == 0) {
                            1
                        } else {
                            cityList[listSize - 1].number + 1
                        }
                    val cityId =weatherDao.insertCity(
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
                    _cityAddId.value = cityId
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            applicationContext.getString(R.string.city_already)+" - ${currentCity[0].number}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun observeAddCity() {
        CoroutineScope(Dispatchers.Default).launch {
            cityAddId.collect {
                if(it != 0L) {
                    _cityAddId.value = 0L
                    val city = weatherDao.getCityById(it)
                    if(city.id != 0L) {
                        if( getWeather(city) ) {
                            if( !getForecast(city) ) {
                                _necessaryLoadAll.value = true
                            }
                        } else {
                            _necessaryLoadAll.value = true
                        }
                        _addCityResult.value = true
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

    private fun reservationMyCity() {
        CoroutineScope(Dispatchers.Default).launch {
            val cityList = loadCityList()
            if( cityList.size==0 ) {
                weatherDao.insertCity(
                    City(
                        1,
                        0,
                        applicationContext.getString(R.string.my_city),
                        0.0,
                        0.0,
                        "",
                        "",
                        ""
                    )
                )
            }
        }
    }

    suspend fun loadCityList() = weatherDao.loadCityList()

    fun deleteCity(city: CityAndWeatherFormated) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.deleteById(city.id)
        }
    }

    fun updateCityList(list: List<CityAndWeatherFormated>) {
        CoroutineScope(Dispatchers.IO).launch {
            var number = 1
            list.map {
                weatherDao.updateCityNumber(it.id, number)
                number++
            }
        }
    }


}