package com.example.weathermaster.data.apiservice.response
import com.google.gson.annotations.SerializedName

class Location : ArrayList<LocationItem>()

class LocationItem(
    @SerializedName("country")
    val country: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("local_names")
    val localNames: Map<String, String>,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String?
)
