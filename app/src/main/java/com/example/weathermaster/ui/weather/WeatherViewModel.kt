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
    val currentTemp: StateFlow<Int> = repository.currentTemp


}