package com.example.weathermaster.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _currentId = MutableStateFlow(0L)
    private val currentId: StateFlow<Long> = _currentId.asStateFlow()

    private val _screenState = MutableStateFlow<WeatherScreenState>(WeatherScreenState.Loading)
    val screenState: StateFlow<WeatherScreenState> = _screenState.asStateFlow()

    fun init() {
        viewModelScope.launch {
            combine(
                repository.listCityAndWeather,
                repository.listCityForecastDay,
                repository.listCityForecastHour,
                currentId
            ) { cityAndWeatherList, cityForecastDayList, cityForecastHour, selectedId ->
                if(selectedId == 0L) {
                    _currentId.value = cityAndWeatherList.firstOrNull()?.id ?: 0L
                }
                val selectedCityAndWeather = cityAndWeatherList.firstOrNull { it.id == selectedId }
                val selectedCityForecastDay = cityForecastDayList.filter { it.idCity == selectedId }
                val selectedCityForecastHour = cityForecastHour.filter { it.idCity == selectedId }

                WeatherScreenState.Loaded(
                    cityAndWeatherList,
                    selectedCityAndWeather,
                    selectedCityForecastDay,
                    selectedCityForecastHour
                )
            }.collect { state ->
                _screenState.value = state
            }
        }
    }

    fun setCurrentId(currentId: Long) {
        _currentId.value = currentId
    }
}