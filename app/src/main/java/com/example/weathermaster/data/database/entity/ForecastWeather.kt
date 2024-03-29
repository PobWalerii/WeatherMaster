package com.example.weathermaster.data.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["idCity","date"])
data class ForecastWeather(
    val idCity: Long,
    val date: String,
    val temp: Double,
    val description: String,
    val icon: String,
)
