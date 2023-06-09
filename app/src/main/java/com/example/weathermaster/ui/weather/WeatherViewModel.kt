package com.example.weathermaster.ui.weather

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val myCity: StateFlow<String> = repository.myCity
    val currentTemp: StateFlow<Double> = repository.currentTemp
    val description: StateFlow<String> = repository.description
    val icon: StateFlow<String> = repository.icon

    val tempSimbol: StateFlow<String> = repository.tempSimbol
    val pressureSimbol: StateFlow<String> = repository.pressureSimbol
    val speedSimbol: StateFlow<String> = repository.speedSimbol
    val humiditySimbol: StateFlow<String> = repository.humiditySimbol
}