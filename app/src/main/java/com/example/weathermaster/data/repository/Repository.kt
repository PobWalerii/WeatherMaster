package com.example.weathermaster.data.repository

import android.content.Context
import android.widget.Toast
import com.example.weathermaster.R
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.*
import com.example.weathermaster.data.apiservice.result.SearchListItem
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.*
import com.example.weathermaster.data.mapers.Mapers.forecastToForecastWeatherDay
import com.example.weathermaster.data.mapers.Mapers.forecastToForecastWeatherHour
import com.example.weathermaster.data.mapers.Mapers.toCityList
import com.example.weathermaster.data.mapers.Mapers.weaterToWeaterFormated
import com.example.weathermaster.notification.NotificationManager
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.KeyConstants.API_KEY
import com.example.weathermaster.workmanager.StartCityGetWorker.cityGetWeatherWorker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: ApiService,
    appSettings: AppSettings,
    private val repoCity: RepoCity,
    private val repoWeather: RepoWeather,
    private val notificationManager: NotificationManager,
    private val applicationContext: Context,
) {

    private val measurement: StateFlow<Int> = appSettings.measurement

    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language
    //private val isConnectStatus: StateFlow<Boolean> = appSettings.isConnectStatus
    //private val isServiceStatus: StateFlow<Boolean> = appSettings.isServiceStatus

    val listCityAndWeather: Flow<List<CityAndWeatherFormated>> = weatherDao.getCityAndWeatherList()
        .map { list ->
            list.map {
                weaterToWeaterFormated(it, measurement.value)
            }
        }

    val listCityForecastHour: Flow<List<ForecastWeatherHour>> = weatherDao.getForecastList()
        .map {list ->
            forecastToForecastWeatherHour(list, measurement.value)
        }

    val listCityForecastDay: Flow<List<ForecastWeatherDay>> = weatherDao.getForecastList()
        .map { list ->
            forecastToForecastWeatherDay(list, measurement.value)
        }

    private val _isLoadData = MutableStateFlow(false)
    val isLoadData: StateFlow<Boolean> = _isLoadData.asStateFlow()

    private val _searchListItem = MutableStateFlow<List<SearchListItem>?>(null)
    val searchListItem: StateFlow<List<SearchListItem>?> = _searchListItem

    private val _addCityResult = MutableStateFlow(false)
    val addCityResult: StateFlow<Boolean> = _addCityResult

    fun init() {
        showNotification()
    }

    private fun showNotification() {
        CoroutineScope(Dispatchers.Default).launch {
            listCityAndWeather.distinctUntilChanged().collect { list ->
                if (list.isNotEmpty()) {
                    notificationManager.updateNotificationContent(list[0])
                }
            }
        }
    }

    fun getSearchList(keyWord: String) {
        CoroutineScope(Dispatchers.IO).launch {
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
            val currentCity = weatherDao.getCityByLocation(current.latitude, current.longitude)
            if (currentCity.isEmpty()) {
                val cityList = weatherDao.loadCityList()
                val listSize = cityList.size
                val number =
                    if (listSize == 0) {
                        1
                    } else {
                        cityList[listSize - 1].number + 1
                    }
                val cityId = repoCity.insertCity(0, number, current)
                if (!repoWeather.getCityWeather(cityId)) {
                    cityGetWeatherWorker(applicationContext, cityId)
                }
                _addCityResult.value = true
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        applicationContext.getString(R.string.city_already),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

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