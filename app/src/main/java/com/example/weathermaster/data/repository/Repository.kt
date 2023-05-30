package com.example.weathermaster.data.repository

import android.content.Context
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.database.dao.WeatherDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService: ApiService,
    private val applicationContext: Context,
) {





}