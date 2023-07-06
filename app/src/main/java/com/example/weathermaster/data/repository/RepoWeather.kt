package com.example.weathermaster.data.repository

import android.content.Context
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.Current
import com.example.weathermaster.data.apiservice.response.Forecast
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.data.database.entity.CurrentWeather
import com.example.weathermaster.data.database.entity.ForecastWeather
import com.example.weathermaster.data.mapers.Mapers
import com.example.weathermaster.utils.KeyConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoWeather @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: ApiService,
    applicationContext: Context,
) {

    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language

    suspend fun getWeatherAll(isForecast: Boolean): Boolean {
        val cityList = weatherDao.loadCityList()
        var isSuccess = true
        cityList.map { city ->
            if (!getWeather(city, isForecast)) {
                isSuccess = false
                return@map
            }
        }
        return isSuccess
    }

    suspend fun getCityWeather(cityId: Long): Boolean {
        val newCity = weatherDao.getCityById(cityId)
        return getWeather(newCity, true)
    }

    private suspend fun getWeather(city: City, forecast: Boolean): Boolean {
        try {
            val response: Current = apiService.getWeather(
                city.latitude,
                city.longitude,
                languageCode,
                KeyConstants.API_KEY
            )
            val current: CurrentWeather = Mapers.weaterResponseToCurrentWeather(response, city.id)
            weatherDao.insertCurrentWeather(current)
            if(forecast) {
                val responseForecast: Forecast = apiService.getForecast(
                    city.latitude,
                    city.longitude,
                    languageCode,
                    KeyConstants.API_KEY
                )
                val forecastWeather: List<ForecastWeather> =
                    Mapers.forecastResponseToForecastWeather(responseForecast, city.id)
                weatherDao.updateForecast(forecastWeather)
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun clearOldForecast(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.clearOldForecast("$date 00:00:00")
        }
    }

}