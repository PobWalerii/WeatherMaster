package com.example.weathermaster.data.apiservice

import com.example.weathermaster.data.apiservice.location.Location
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("geo/1.0/reverse")
    suspend fun getCity(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Location

}