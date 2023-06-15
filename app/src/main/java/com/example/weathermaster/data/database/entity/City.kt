package com.example.weathermaster.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val countryName: String,
    val state: String
)
