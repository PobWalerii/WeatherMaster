package com.example.weathermaster.ui.weather

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.apiservice.result.CurrentForecast
import com.example.weathermaster.data.apiservice.result.CurrentWeather
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val myCity: StateFlow<String> = repository.myCity
    val currentWeather: StateFlow<CurrentWeather?> = repository.currentWeather
    val currentForecast: StateFlow<List<CurrentForecast>?> = repository.currentForecast

    val tempSimbol: StateFlow<String> = repository.tempSimbol
    //val pressureSimbol: StateFlow<String> = repository.pressureSimbol
    //val speedSimbol: StateFlow<String> = repository.speedSimbol
    //val humiditySimbol: StateFlow<String> = repository.humiditySimbol
}