package com.example.weathermaster.data.database.entity

data class ForecastWeatherHour (
    val idCity: Long,
    val hour: String,
    val dn: String,
    val temp: Float,
    val tempString: String,
    val icon: String?
)