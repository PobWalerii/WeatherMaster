package com.example.weathermaster.data.mapers

import com.example.weathermaster.data.apiservice.response.Location
import com.example.weathermaster.data.apiservice.response.LocationItem
import com.example.weathermaster.data.apiservice.result.SearchListItem
import java.util.*

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
                localNames.get(languageCode) ?: item.name
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
}