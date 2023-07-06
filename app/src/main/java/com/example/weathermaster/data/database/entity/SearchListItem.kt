package com.example.weathermaster.data.database.entity

data class SearchListItem(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val countryName: String,
    val state: String
)
