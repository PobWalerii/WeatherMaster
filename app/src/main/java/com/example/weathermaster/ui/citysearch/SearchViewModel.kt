package com.example.weathermaster.ui.citysearch

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.apiservice.result.SearchListItem
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val isLoadData: StateFlow<Boolean> = repository.isLoadData
    val searchListItem: StateFlow<List<SearchListItem>?> = repository.searchListItem
    val addCityResult: StateFlow<Boolean> = repository.addCityResult

    init {
        repository.searchListToNull()
        repository.setAddCityResult()
    }

    fun getSearchList(keyWord: String) {
        repository.getSearchList(keyWord)
    }

    fun addCity(current: SearchListItem) {
        repository.addCity(current)
    }
}