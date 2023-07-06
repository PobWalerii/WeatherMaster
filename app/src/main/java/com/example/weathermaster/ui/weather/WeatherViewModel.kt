package com.example.weathermaster.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.data.database.entity.ForecastWeatherDay
import com.example.weathermaster.data.database.entity.ForecastWeatherHour
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    repository: Repository
): ViewModel() {

    private val _currentId = MutableStateFlow(0L)
    private val currentId: StateFlow<Long> = _currentId.asStateFlow()

    val listCityAndWeather: Flow<List<CityAndWeatherFormated>> = repository.listCityAndWeather
    private val listCityForecastDay: Flow<List<ForecastWeatherDay>> = repository.listCityForecastDay
    val listCityForecastHour: Flow<List<ForecastWeatherHour>> = repository.listCityForecastHour

    val selectedCityAndWeather: Flow<CityAndWeatherFormated?> = combine(
        listCityAndWeather,
        currentId
    ) { cityAndWeatherList, selectedId ->
        val city = cityAndWeatherList.firstOrNull { it.id == selectedId }
        city
    }

    val selectedCityForecastDay: Flow<List<ForecastWeatherDay>> = combine(
        listCityForecastDay,
        currentId
    ) { forecastDayList, selectedId ->
        forecastDayList.filter { it.idCity == selectedId }
    }

    val selectedCityForecastHour: Flow<List<ForecastWeatherHour>> = combine(
        listCityForecastHour,
        currentId
    ) { forecastHourList, selectedId ->
        forecastHourList.filter { it.idCity == selectedId }
    }

    init {
        viewModelScope.launch {
            listCityAndWeather.collect { cityAndWeatherList ->
                val firstCityId = cityAndWeatherList.firstOrNull()?.id ?: 0L
                _currentId.value = firstCityId
            }
        }
    }

    fun setCurrentId(currentId: Long) {
        _currentId.value = currentId
    }






}