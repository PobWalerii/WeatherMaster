package com.example.weathermaster.ui.citylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _cityList: MutableStateFlow<List<CityAndWeatherFormated>> = MutableStateFlow(emptyList())
    val cityList: StateFlow<List<CityAndWeatherFormated>> = _cityList.asStateFlow()

    val addCityResult: StateFlow<Boolean> = repository.addCityResult

    fun loadCityList() {
        viewModelScope.launch {
            _cityList.value = repository.listCityAndWeather.first().filter { it.number != 0 }
        }
    }

    fun deleteCity(city: CityAndWeatherFormated) {
        repository.deleteCity(city)
    }

    fun updateCityList(list: List<CityAndWeatherFormated>) {
        repository.updateCityList(list)
    }

    fun isScrolled() {
        repository.setAddCityResult()
    }

}