package com.example.weathermaster.ui.weather

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.data.database.entity.ForecastWeatherDay
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    var currentId = 0L
    val listCityAndWeather: Flow<List<CityAndWeatherFormated>> = repository.listCityAndWeather
    val listCityForecastDay: Flow<List<ForecastWeatherDay>> = repository.listCityForecastDay

}