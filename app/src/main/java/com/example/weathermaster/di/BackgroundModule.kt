package com.example.weathermaster.di

import android.content.Context
import com.example.weathermaster.workmanager.DataUpdateManager
import com.example.weathermaster.settings.AppSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackgroundModule {

    @Singleton
    @Provides
    fun provideDataUpdateManager(
        appSettings: AppSettings,
        @ApplicationContext applicationContext: Context,
    ): DataUpdateManager {
        return DataUpdateManager(appSettings, applicationContext)
    }

}