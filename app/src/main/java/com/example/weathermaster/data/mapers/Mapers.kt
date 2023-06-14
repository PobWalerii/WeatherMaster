package com.example.weathermaster.data.mapers

import com.example.weathermaster.data.apiservice.response.Location
import com.example.weathermaster.data.apiservice.result.SearchList
import java.util.*

object Mapers {

    fun toCityList(response: Location, languageCode: String): List<SearchList> {
        val list: MutableList<SearchList> = mutableListOf()
        response.map {
            val localNames: Map<String, String> = it.localNames
            val city =
                try {
                    localNames.get(languageCode) ?: it.name
                } catch (e: Exception) {
                    it.name
                }
            val countryName: String = Locale("", it.country).displayCountry
            list.add(
                SearchList(
                    city,
                    it.lat,
                    it.lon,
                    it.country,
                    countryName
                )
            )
        }
        return list.toList()
    }
}