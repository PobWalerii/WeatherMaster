package com.example.weathermaster.di

import android.content.Context
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.repository.Repository
import com.example.weathermaster.geolocation.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun provideLocationManager(
        @ApplicationContext applicationContext: Context,
    ): LocationManager {
        return LocationManager(applicationContext)
    }

}