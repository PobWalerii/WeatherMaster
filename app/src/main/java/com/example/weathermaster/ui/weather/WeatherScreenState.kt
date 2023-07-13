package com.example.weathermaster.ui.weather

import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.data.database.entity.ForecastWeatherDay
import com.example.weathermaster.data.database.entity.ForecastWeatherHour

sealed class WeatherScreenState {
    object Loading : WeatherScreenState()
    data class Loaded(
        val currentCityList: List<CityAndWeatherFormated>,
        val selectedCityAndWeather: CityAndWeatherFormated?,
        val selectedCityForecastDay: List<ForecastWeatherDay>,
        val selectedCityForecastHour: List<ForecastWeatherHour>,
        val isPermission: Boolean
    ) : WeatherScreenState()
}