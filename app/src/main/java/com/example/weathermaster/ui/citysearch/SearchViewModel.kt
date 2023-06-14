package com.example.weathermaster.ui.citysearch

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.apiservice.result.CurrentForecast
import com.example.weathermaster.data.apiservice.result.CurrentWeather
import com.example.weathermaster.data.apiservice.result.SearchList
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val isLoadData: StateFlow<Boolean> = repository.isLoadData
    val searchList: StateFlow<List<SearchList>> = repository.searchList

    var _waitingData = MutableStateFlow(false)
    val waitingData: StateFlow<Boolean> = _waitingData.asStateFlow()

    fun getSearchList(keyWord: String) {
        _waitingData.value = true
        repository.getSearchList(keyWord)
    }
}