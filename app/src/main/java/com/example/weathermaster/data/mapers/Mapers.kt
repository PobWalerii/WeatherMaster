package com.example.weathermaster.data.mapers

import android.content.Context
import android.os.Build
import com.example.weathermaster.R
import com.example.weathermaster.data.apiservice.response.Current
import com.example.weathermaster.data.apiservice.response.Forecast
import com.example.weathermaster.data.apiservice.response.Location
import com.example.weathermaster.data.apiservice.response.LocationItem
import com.example.weathermaster.data.database.entity.SearchListItem
import com.example.weathermaster.data.database.entity.*
import com.example.weathermaster.utils.KeyConstants.FORECAST_HOUR_STEP
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

    fun weaterToWeaterFormated(it: CityAndWeather, measurement: Int, context: Context): CityAndWeatherFormated {

        val tempSimbol = listOf("K","\u2103","\u2109")[measurement-1]
        val speedSimbol = listOf(" m/s"," m/s"," mph")[measurement-1]
        val pressureSimbol = " hPa"
        val humiditySimbol = " %"
        val emptyCity = it.id == 1L && it.cityName == context.getString(R.string.my_city)
        return CityAndWeatherFormated(
            it.id,
            it.number,
            it.cityName,
            it.latitude,
            it.longitude,
            it.country,
            it.countryName,
            it.state,
            if(emptyCity) "" else ((convertTemp(it.temp, measurement) * 10.0).roundToLong() / 10.0).toString(),
            if(emptyCity) "" else tempSimbol,
            it.description ?: "",
            it.icon ?: "",
            if(emptyCity) "" else "Pressure " + it.pressure.toString() + pressureSimbol +
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

        val groupedData = list.filter { it.date.substring(0, 10) >= currentDate }
            .groupBy { Pair(it.idCity, it.date.substring(0, 10)) }

        groupedData.forEach { (key, values) ->

            val (idCity, dateForecast) = key

            val minTemp = values.minByOrNull { it.temp }?.temp ?: 0.0
            val maxTemp = values.maxByOrNull { it.temp }?.temp ?: 0.0

            val descriptionCounts = values.groupBy { it.description }
                .mapValues { (_, items) -> items.size }

            var maxCount = 0
            var dominantDescription = ""
            for ((description, count) in descriptionCounts) {
                if (count > maxCount) {
                    dominantDescription = description
                    maxCount = count
                }
            }

            val iconCounts = values.groupBy { it.icon }
                .mapValues { (_, items) -> items.size }

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
                    idCity,
                    dateForecast,
                    getDn(dateForecast).replaceFirstChar { it.uppercase() },
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
        return forecastDay
    }

    fun forecastToForecastWeatherHour(list: List<ForecastWeather>, measurement: Int): List<ForecastWeatherHour> {

        val forecastHour: MutableList<ForecastWeatherHour> = mutableListOf()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date().time - FORECAST_HOUR_STEP*3600*1000L
        val currentTime = dateFormat.format(date).substring(0,14)+"00:00"

        list.filter { it.date >= currentTime }.map {
            val temp = ((convertTemp(it.temp, measurement) * 10.0).roundToLong() / 10.0)
            forecastHour.add(
                ForecastWeatherHour(
                    it.idCity,
                    it.date.substring(11,16),
                    getDn(it.date.substring(0,10)).replaceFirstChar { day -> day.uppercase() },
                    temp.toFloat(),
                    temp.toString() + listOf(
                        "K",
                        "\u2103",
                        "\u2109"
                    )[measurement - 1],
                    it.icon
                )
            )

        }
        return forecastHour
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