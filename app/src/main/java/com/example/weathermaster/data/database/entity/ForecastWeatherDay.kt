package com.example.weathermaster.data.database.entity

data class ForecastWeatherDay (
    val idCity: Long,
    val date: String,
    val dn: String,
    val temp: String,
    val description: String?,
    val icon: String?
)