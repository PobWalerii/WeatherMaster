package com.example.weathermaster.data.repository

import android.content.Context
import android.widget.Toast
import com.example.weathermaster.R
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.apiservice.response.LocationItem
import com.example.weathermaster.data.database.entity.SearchListItem
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.data.mapers.Mapers
import com.example.weathermaster.utils.KeyConstants
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoCity @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: ApiService,
    private val applicationContext: Context,
) {

    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language

    init {
        reservationMyCity()
    }

    private fun reservationMyCity() {
        CoroutineScope(Dispatchers.Default).launch {
            val cityList = weatherDao.loadCityList()
            if( cityList.isEmpty() ) {
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

    suspend fun setCurrentCity(latitude: Double, longitude: Double): Boolean {
        return try {
            val city: SearchListItem? = getCityFromLocation(latitude, longitude)
            if (city != null) {
                insertCity(1, 0, city)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun insertCity(newId: Long, newNumber: Int, city: SearchListItem): Long {
        return weatherDao.insertCity(
            City(
                newId,
                newNumber,
                city.cityName,
                city.latitude,
                city.longitude,
                city.country,
                city.countryName,
                city.state
            )
        )
    }

    private suspend fun getCityFromLocation(latitude: Double, longitude: Double): SearchListItem? {
        return try {
            val response: LocationItem = apiService.getCity(
                latitude,
                longitude,
                1, KeyConstants.API_KEY
            )[0]
            Mapers.getCityFromResponse(response, languageCode)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            null
        }
    }

}