package com.example.weathermaster.data.repository

import android.content.Context
import android.widget.Toast
import com.example.weathermaster.R
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.*
import com.example.weathermaster.data.database.entity.SearchListItem
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.*
import com.example.weathermaster.data.mapers.Mapers.forecastToForecastWeatherDay
import com.example.weathermaster.data.mapers.Mapers.forecastToForecastWeatherHour
import com.example.weathermaster.data.mapers.Mapers.toCityList
import com.example.weathermaster.data.mapers.Mapers.weaterToWeaterFormated
import com.example.weathermaster.geolocation.LocationProvider
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.ui.citysearch.SearchCityState
import com.example.weathermaster.utils.KeyConstants.API_KEY
import com.example.weathermaster.utils.NetworkStatus.connectStatus
import com.example.weathermaster.workmanager.CityStartWorker.cityGetWeatherWorker
import com.example.weathermaster.workmanager.CityStartWorker.getCurrentCityWorker
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
    private val applicationContext: Context,
) {

    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language

    val measurement: StateFlow<Int> = appSettings.measurement

    val listCityAndWeather: Flow<List<CityAndWeatherFormated>> = weatherDao.getCityAndWeatherList()
        .map { list ->
            list.map {
                weaterToWeaterFormated(it, measurement.value, applicationContext)
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

    val currentData: Flow<CityAndWeatherFormated> = listCityAndWeather
        .map { list ->
            list[0]
        }

    private val _addCityResult = MutableStateFlow(false)
    val addCityResult: StateFlow<Boolean> = _addCityResult

    private val _searchState = MutableStateFlow<SearchCityState>(SearchCityState.Loaded)
    val searchState: StateFlow<SearchCityState> = _searchState

    val isPermission: StateFlow<Boolean> = appSettings.isPermission
    val isStartedApp: StateFlow<Boolean> = appSettings.isStartedApp

    fun init() {
        checkCurrentLocation()
    }

    private fun checkCurrentLocation() {
        CoroutineScope(Dispatchers.IO).launch {
            isStartedApp.collect {
                if (it) {
                    if( !connectStatus(applicationContext) ) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                R.string.text_no_internet,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    if( isPermission.value ) {
                        val locationProvider = LocationProvider(applicationContext)
                        val location = locationProvider.getLastKnownLocation()
                        if (location != null) {
                            if (repoCity.setCurrentCity(location.latitude, location.longitude)) {
                                if (!repoWeather.getCityWeather(1L)) {
                                    cityGetWeatherWorker(applicationContext, 1L)
                                }
                            } else {
                                getCurrentCityWorker(applicationContext)
                            }
                        } else {
                            getCurrentCityWorker(applicationContext)
                        }
                    }
                }
            }
        }
    }

    fun getSearchList(keyWord: String) {
        MainScope().launch {
            _searchState.value = SearchCityState.Loading
            try {
                withContext(Dispatchers.IO) {
                    val response: Location =
                        apiService.getSearchList(
                            keyWord,
                            10,
                            API_KEY
                        )
                    val list: List<SearchListItem> = toCityList(response, languageCode)
                    _searchState.value = if (list.isNotEmpty()) {
                        SearchCityState.Success(list)
                    } else {
                        SearchCityState.Empty
                    }
                }
            } catch (e: Exception) {
                _searchState.value = SearchCityState.Error(
                    applicationContext.getString(
                        if (connectStatus(applicationContext)) {
                            R.string.no_servise_connect
                        } else {
                            R.string.text_need_internet
                        }
                    )
                )
            } finally {
                _searchState.value = SearchCityState.Loaded
            }
        }
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