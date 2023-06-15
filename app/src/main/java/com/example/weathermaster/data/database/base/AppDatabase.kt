package com.example.weathermaster.data.database.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.database.entity.City

@Database(entities = [City::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}