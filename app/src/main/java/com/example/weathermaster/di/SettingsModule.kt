package com.example.weathermaster.di

import android.content.Context
import com.example.weathermaster.settings.AppSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Singleton
    @Provides
    fun provideAppSettings(
        @ApplicationContext applicationContext: Context
    ): AppSettings {
        return AppSettings(applicationContext)
    }
}