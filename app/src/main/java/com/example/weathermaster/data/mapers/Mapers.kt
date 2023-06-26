package com.example.weathermaster.data.mapers

import android.os.Build
import com.example.weathermaster.data.apiservice.response.Current
import com.example.weathermaster.data.apiservice.response.Forecast
import com.example.weathermaster.data.apiservice.response.Location
import com.example.weathermaster.data.apiservice.response.LocationItem
import com.example.weathermaster.data.apiservice.result.SearchListItem
import com.example.weathermaster.data.database.entity.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.roundToLong

object Mapers {

    fun toCityList(response: Location, languageCode: String): List<SearchListItem> {
        val list: MutableList<SearchListItem> = mutableListOf()
        response.map {
            val city = getCityFromResponse(it, languageCode)
            list.add(city)
        }
        return list.toList()
    }

    fun getCityFromResponse(item: LocationItem, languageCode: String): SearchListItem {
        val localNames: Map<String, String> = item.localNames
        val city: String =
            try {
                localNames[languageCode] ?: item.name
            } catch (e: Exception) {
                item.name
            }
        val countryName: String = Locale("", item.country).displayCountry
        val state: String = item.state ?: ""
        return SearchListItem(
            city,
            item.lat,
            item.lon,
            item.country,
            countryName,
            state
        )
    }

    fun forecastResponseToForecastWeather(response: Forecast, idCity: Long): List<ForecastWeather> {
        val forecast: MutableList<ForecastWeather> = mutableListOf()
        response.listData.map {
            val main = it.main
            val weat = it.weather[0]
            forecast.add(
                ForecastWeather(
                    idCity,
                    it.dtTxt,
                    main.temp,
                    weat.description,
                    weat.icon
                )
            )
        }
        return forecast
    }

    fun weaterResponseToCurrentWeather(response: Current, idCity: Long): CurrentWeather {
        val main = response.main
        val weat = response.weather[0]
        val wind = response.wind
        return CurrentWeather(
            idCity,
            main.temp,
            weat.description.replaceFirstChar { it.uppercase() },
            weat.icon,
            main.pressure,
            main.humidity,
            wind.speed,
            wind.gust,
        )
    }

    fun weaterToWeaterFormated(it: CityAndWeather, measurement: Int): CityAndWeatherFormated {

        val tempSimbol = listOf("K","\u2103","\u2109")[measurement-1]
        val speedSimbol = listOf(" m/s"," m/s"," mph")[measurement-1]
        val pressureSimbol = " hPa"
        val humiditySimbol = " %"

        return CityAndWeatherFormated(
            it.id,
            it.number,
            it.cityName,
            it.latitude,
            it.longitude,
            it.country,
            it.countryName,
            it.state,
            ((convertTemp(it.temp, measurement) * 10.0).roundToLong() / 10.0).toString(),
            tempSimbol,
            it.description ?: "",
            it.icon ?: "",
            "Pressure " + it.pressure.toString() + pressureSimbol +
                    "\nHumidity " + it.humidity.toString() + humiditySimbol +
                    "\nWind: Speed " + ((convertSpeed(it.windSpeed, measurement) * 10.0).roundToLong() / 10.0).toString() + speedSimbol +
                    ", Gust " + ((convertSpeed(it.windGust, measurement) * 10.0).roundToLong() / 10.0).toString() + speedSimbol
        )
    }

    private fun convertTemp(temp: Double, measurement: Int): Double {
        return when(measurement) {
            2 -> temp - 273.15
            3 -> (temp - 273.15) * 9 / 5 + 32
            else -> temp
        }
    }

    private fun convertSpeed(speed: Double, measurement: Int): Double {
        return if(measurement == 3) {
            speed * 2.23694
        } else {
            speed
        }
    }

    fun forecastToForecastWeatherDay(list: List<ForecastWeather>, measurement: Int): List<ForecastWeatherDay> {

        val forecastDay: MutableList<ForecastWeatherDay> = mutableListOf()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        var startId = 0L
        var startDate = "0000-00-00"
        val descriptionCounts = mutableMapOf<String, Int>()
        val iconCounts = mutableMapOf<String, Int>()
        var minTemp = 0.0
        var maxTemp = 0.0

        list.filter { it.date.substring(0,10) >= currentDate }.map { item ->

            if(item.idCity != startId || item.date.substring(0,10) != startDate) {
                if(startDate != "0000-00-00") {
                    var maxCount = 0
                    var dominantDescription = ""
                    for ((description, count) in descriptionCounts) {
                        if (count > maxCount) {
                            dominantDescription = description
                            maxCount = count
                        }
                    }
                    maxCount = 0
                    var dominantIcon = ""
                    for ((icon, count) in iconCounts) {
                        if (count > maxCount) {
                            dominantIcon = icon
                            maxCount = count
                        }
                    }

                    forecastDay.add(
                        ForecastWeatherDay(
                            startId,
                            startDate,
                            getDn(startDate).replaceFirstChar { it.uppercase() },
                            ((convertTemp(minTemp, measurement) * 10.0).roundToLong() / 10.0).toString() + " / " +
                                    ((convertTemp(maxTemp, measurement) * 10.0).roundToLong() / 10.0).toString() + listOf(
                                "K",
                                "\u2103",
                                "\u2109"
                            )[measurement - 1],
                            dominantDescription.replaceFirstChar { it.uppercase() },
                            dominantIcon
                        )
                    )
                }
                startId = item.idCity
                startDate = item.date.substring(0, 10)
                descriptionCounts.clear()
                iconCounts.clear()
                minTemp = item.temp
                maxTemp = item.temp
            } else {
                if(item.temp<minTemp) minTemp = item.temp
                if(item.temp>maxTemp) maxTemp = item.temp
                var counts = iconCounts.getOrDefault(item.icon, 0)
                iconCounts[item.icon] = counts + 1
                counts = descriptionCounts.getOrDefault(item.description, 0)
                descriptionCounts[item.description] = counts + 1
            }
        }
        return forecastDay
    }

    private fun getDn(startDate: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = LocalDate.parse(startDate)
            val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            dayOfWeek
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(startDate)
            val calendar = Calendar.getInstance()
            calendar.time = date!!
            val displayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            displayName
        }
    }

}