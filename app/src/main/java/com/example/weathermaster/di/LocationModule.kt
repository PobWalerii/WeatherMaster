package com.example.weathermaster.di

import android.content.Context
import com.example.weathermaster.geolocation.LocationManager
import com.example.weathermaster.settings.AppSettings
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
        appSettings: AppSettings,
        @ApplicationContext applicationContext: Context,
    ): LocationManager {
        return LocationManager(appSettings, applicationContext)
    }


}