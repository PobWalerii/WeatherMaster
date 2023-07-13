package com.example.weathermaster.di

import android.content.Context
import androidx.room.Room
import com.example.weathermaster.data.apiservice.ApiService
import com.example.weathermaster.data.database.base.AppDatabase
import com.example.weathermaster.data.database.dao.WeatherDao
import com.example.weathermaster.data.repository.RepoCity
import com.example.weathermaster.data.repository.RepoWeather
import com.example.weathermaster.data.repository.Repository
import com.example.weathermaster.settings.AppSettings
import com.example.weathermaster.utils.KeyConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            KeyConstants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideWeatherDao(database: AppDatabase): WeatherDao {
        return database.weatherDao()
    }

    @Singleton
    @Provides
    fun provideRepository(
        weatherDao: WeatherDao,
        apiService: ApiService,
        appSettings: AppSettings,
        repoCity: RepoCity,
        repoWeather: RepoWeather,
        @ApplicationContext applicationContext: Context,
    ): Repository {
        return Repository(weatherDao,
            apiService,
            appSettings,
            repoCity,
            repoWeather,
            applicationContext)
    }

    @Singleton
    @Provides
    fun provideRepoCity(
        weatherDao: WeatherDao,
        apiService: ApiService,
        @ApplicationContext applicationContext: Context,
    ): RepoCity {
        return RepoCity(weatherDao,
            apiService,
            applicationContext)
    }

    @Singleton
    @Provides
    fun provideRepoWeather(
        weatherDao: WeatherDao,
        apiService: ApiService,
        @ApplicationContext applicationContext: Context,
    ): RepoWeather {
        return RepoWeather(weatherDao,
            apiService,
            applicationContext)
    }

}
