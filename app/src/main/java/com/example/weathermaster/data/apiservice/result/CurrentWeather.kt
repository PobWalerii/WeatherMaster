package com.example.weathermaster.data.apiservice.result

data class CurrentWeather(
    val temp: String,
    val description: String,
    val icon: String,
    val pressure: String,
    val humidity: String,
    val windSpeed: String,
    val windGust: String,
)
