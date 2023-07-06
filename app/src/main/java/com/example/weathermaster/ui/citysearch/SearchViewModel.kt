package com.example.weathermaster.ui.citysearch

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.database.entity.SearchListItem
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val searchState: StateFlow<SearchCityState> = repository.searchState

    val addCityResult: StateFlow<Boolean> = repository.addCityResult

    init {
        repository.setAddCityResult()
    }

    fun getSearchList(keyWord: String) {
        repository.getSearchList(keyWord)
    }

    fun addCity(current: SearchListItem) {
        repository.addCity(current)
    }
}