package com.example.weathermaster.ui.citysearch

import com.example.weathermaster.data.database.entity.SearchListItem

sealed class SearchCityState {
    object Loading : SearchCityState()
    data class Success(val searchList: List<SearchListItem>) : SearchCityState()
    object Empty : SearchCityState()
    data class Error(val errorMessage: String) : SearchCityState()
    object Loaded : SearchCityState()
}
