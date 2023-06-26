package com.example.weathermaster.data.database.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.data.database.entity.CurrentWeather
import com.example.weathermaster.data.database.entity.ForecastWeather

@Database(entities = [City::class, CurrentWeather::class, ForecastWeather::class], version = 10, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}