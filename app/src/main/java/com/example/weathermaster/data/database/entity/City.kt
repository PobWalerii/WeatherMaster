package com.example.weathermaster.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val cityMain: Boolean,
    val latitude: Double,
    val longitude: Double
)
