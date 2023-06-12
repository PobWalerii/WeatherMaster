package com.example.weathermaster.data.apiservice.result

data class CurrentForecast (
    val date: String,
    val dn: String,
    val temp: String,
    val description: String,
    val icon: String
)