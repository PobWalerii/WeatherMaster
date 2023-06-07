package com.example.weathermaster.data.repository

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.weathermaster.data.apiservice.ApiService

import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.KeyConstants.API_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: ApiService,
    private val appSettings: AppSettings,
    private val applicationContext: Context,
) {

    private val latitude: StateFlow<Double> = appSettings.latitude
    private val longitude: StateFlow<Double> = appSettings.longitude
    private val languageCode: String = applicationContext.resources.configuration.locales.get(0).language

    fun init() {
        observeLocation()
    }

    private fun observeLocation() {
        CoroutineScope(Dispatchers.Default).launch {
            combine(
                latitude,
                longitude
            ) { lat, lon -> Pair(lat, lon) }.collect { (lat, lon) ->
                getSity(lat, lon)
            }
        }
    }

    private suspend fun getSity(lat: Double, lon: Double) {
        try {
            val response = apiService.getCity(lat,lon,1, API_KEY)
            val sity = response[0].local_names//.javaClass.getDeclaredField(languageCode).get(0) as String
            withContext(Dispatchers.Main) {
                //Toast.makeText(applicationContext, "$lat,$lon", Toast.LENGTH_LONG).show()
                Toast.makeText(applicationContext, "$sity", Toast.LENGTH_LONG).show()
            }


        } catch (e: Exception) {
            val message = e.message
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
            }
        }
    }






    /*
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations){
                val latitude = location.latitude.toString()
                val longitude = location.longitude.toString()
                showLocation(latitude,longitude)
            }
        }
    }
    private fun showLocation(latitude: String,longitude:String) {
        Toast.makeText(applicationContext,"$latitude, $longitude",Toast.LENGTH_SHORT).show()
    }

    fun init(activity: Activity) {
        val permissionGranted = locationManager.requestLocationPermission(activity)
        if (permissionGranted) {
            locationManager.startLocationTracking(locationCallback)
        }
    }

     */

}