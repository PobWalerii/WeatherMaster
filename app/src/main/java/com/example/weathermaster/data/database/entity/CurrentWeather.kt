package com.example.weathermaster.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentWeather(
    @PrimaryKey
    val idcity: Long,
    val temp: Double,
    val description: String,
    val icon: String,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windGust: Double,
)