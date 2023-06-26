package com.example.weathermaster.data.database.entity

data class CityAndWeather(
    val id: Long,
    val number: Int,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val countryName: String,
    val state: String,

    val temp: Double,
    val description: String?,
    val icon: String?,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windGust: Double,
)


