package com.example.weathermaster.data.apiservice.result

data class SearchList(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val countryName: String,
)
