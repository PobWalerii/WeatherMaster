package com.example.weathermaster.data.database.dao

import androidx.room.*
import com.example.weathermaster.data.database.entity.City
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM City")
    fun loadCityList(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City): Long

    @Delete
    suspend fun deleteCity(city: City)

    @Query("DELETE FROM City")
    suspend fun deleteAll()

    @Transaction
    suspend fun updateCityList(list: List<City>) {
        deleteAll()
        list.map {
            insertCity(it)
        }
    }

}