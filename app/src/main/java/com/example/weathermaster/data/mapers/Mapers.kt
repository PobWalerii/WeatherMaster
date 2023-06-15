package com.example.weathermaster.data.mapers

import com.example.weathermaster.data.apiservice.response.Location
import com.example.weathermaster.data.apiservice.result.SearchListItem
import java.util.*

object Mapers {

    fun toCityList(response: Location, languageCode: String): List<SearchListItem> {
        val list: MutableList<SearchListItem> = mutableListOf()
        response.map {
            val localNames: Map<String, String> = it.localNames
            val city: String =
                try {
                    localNames.get(languageCode) ?: it.name
                } catch (e: Exception) {
                    it.name
                }
            val countryName: String = Locale("", it.country).displayCountry
            val state: String = it.state ?: ""
            list.add(
                SearchListItem(
                    city,
                    it.lat,
                    it.lon,
                    it.country,
                    countryName,
                    state
                )
            )
        }
        return list.toList()
    }
}