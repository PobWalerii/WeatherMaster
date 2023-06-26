package com.example.weathermaster.data.database.dao

import androidx.room.*
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.data.database.entity.CityAndWeather
import com.example.weathermaster.data.database.entity.CurrentWeather
import com.example.weathermaster.data.database.entity.ForecastWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM City ORDER BY number")
    suspend fun loadCityList(): List<City>

    @Query("SELECT * FROM City WHERE latitude = :lat AND longitude = :lon")
    suspend fun getCityByLocation(lat: Double, lon: Double): List<City>

    @Query("SELECT * FROM City WHERE id = :cityId")
    suspend fun getCityById(cityId: Long): City

    @Query("UPDATE City SET number = :newNumber WHERE id = :cityId")
    suspend fun updateCityNumber(cityId: Long, newNumber: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City): Long

    @Transaction
    suspend fun deleteById(id: Long) {
        deleteCity(id)
        deleteWeather(id)
    }

    @Query("DELETE FROM City WHERE id = :curId")
    suspend fun deleteCity(curId: Long)

    @Query("DELETE FROM CurrentWeather WHERE idCity = :curId")
    suspend fun deleteWeather(curId: Long)


    @Query("SELECT T1.*, T2.* FROM City T1 LEFT JOIN CurrentWeather T2 ON T1.id = T2.idCity ORDER BY T1.number")
    fun getCityAndWeatherList(): Flow<List<CityAndWeather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather)

    @Transaction
    suspend fun updateForecast(forecast: List<ForecastWeather>) {
        forecast.map { timestamp ->
            insertCityForecast(timestamp)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityForecast(timestamp: ForecastWeather)

    @Query("SELECT * FROM ForecastWeather ORDER BY idCity, date")
    fun getForecastList(): Flow<List<ForecastWeather>>

}