package com.example.weathermaster.data.apiservice.response
import com.google.gson.annotations.SerializedName

class Forecast(
    @SerializedName("city")
    val city: City,
    @SerializedName("cnt")
    val cnt: Int,
    @SerializedName("cod")
    val cod: String,
    @SerializedName("list")
    val listData: List<WeatherData>,
    @SerializedName("message")
    val message: Int
)

class City(
    @SerializedName("coord")
    val coord: CoordForecast,
    @SerializedName("country")
    val country: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("population")
    val population: Int,
    @SerializedName("sunrise")
    val sunrise: Int,
    @SerializedName("sunset")
    val sunset: Int,
    @SerializedName("timezone")
    val timezone: Int
)

class WeatherData(
    @SerializedName("clouds")
    val clouds: CloudsForecast,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("dt_txt")
    val dtTxt: String,
    @SerializedName("main")
    val main: MainForecast,
    @SerializedName("pop")
    val pop: Double,
    @SerializedName("rain")
    val rain: Rain,
    @SerializedName("sys")
    val sys: SysForecast,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("weather")
    val weather: List<WeatherForecast>,
    @SerializedName("wind")
    val wind: WindForecast
)

class CoordForecast(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double
)

class CloudsForecast(
    @SerializedName("all")
    val all: Int
)

class MainForecast(
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("grnd_level")
    val grndLevel: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("sea_level")
    val seaLevel: Int,
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("temp_kf")
    val tempKf: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("temp_min")
    val tempMin: Double
)

class Rain(
    @SerializedName("3h")
    val h: Double
)

class SysForecast(
    @SerializedName("pod")
    val pod: String
)

class WeatherForecast(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String
)

class WindForecast(
    @SerializedName("deg")
    val deg: Int,
    @SerializedName("gust")
    val gust: Double,
    @SerializedName("speed")
    val speed: Double
)
