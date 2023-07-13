package com.example.weathermaster.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermaster.data.database.entity.ForecastWeatherDay
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _currentId = MutableStateFlow(1L)
    private val currentId: StateFlow<Long> = _currentId.asStateFlow()

    private val _screenState = MutableStateFlow<WeatherScreenState>(WeatherScreenState.Loading)
    val screenState: StateFlow<WeatherScreenState> = _screenState.asStateFlow()

    fun init() {

        viewModelScope.launch {
            combine(
                repository.listCityAndWeather,
                repository.listCityForecastDay,
                repository.listCityForecastHour,
                repository.isPermission,
                currentId
            ) { cityAndWeatherList, cityForecastDayList, cityForecastHour, isPermission, selectedId ->

                val selectedCityAndWeather = cityAndWeatherList.firstOrNull { it.id == selectedId }
                if(selectedCityAndWeather == null) {
                    setCurrentId(1L)
                }
                var selectedCityForecastDay = cityForecastDayList.filter { it.idCity == selectedId }
                val selectedCityForecastHour = cityForecastHour.filter { it.idCity == selectedId }

                if (selectedCityForecastDay.isEmpty()) {
                    val item = ForecastWeatherDay(0,"","","","","")
                    selectedCityForecastDay = listOf(item,item,item,item)
                }
                WeatherScreenState.Loaded(
                    cityAndWeatherList,
                    selectedCityAndWeather,
                    selectedCityForecastDay,
                    selectedCityForecastHour,
                    isPermission
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