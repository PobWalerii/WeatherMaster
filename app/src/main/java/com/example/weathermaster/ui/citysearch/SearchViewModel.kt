package com.example.weathermaster.ui.citysearch

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.apiservice.result.CurrentForecast
import com.example.weathermaster.data.apiservice.result.CurrentWeather
import com.example.weathermaster.data.apiservice.result.SearchList
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val isLoadData: StateFlow<Boolean> = repository.isLoadData
    val searchList: StateFlow<List<SearchList>> = repository.searchList

    fun getSearchList(keyWord: String) {
        repository.getSearchList(keyWord)
    }
}