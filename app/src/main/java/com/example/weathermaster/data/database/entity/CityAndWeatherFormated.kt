package com.example.weathermaster.data.database.entity

data class CityAndWeatherFormated(
    val id: Long = 0,
    val number: Int,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val countryName: String,
    val state: String,

    val temp: String,
    val tempSimbol: String,
    val description: String,
    val icon: String,
    val weather: String,
)