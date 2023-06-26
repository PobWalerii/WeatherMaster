package com.example.weathermaster.ui.citylist

import androidx.lifecycle.ViewModel
import com.example.weathermaster.data.database.entity.City
import com.example.weathermaster.data.database.entity.CityAndWeatherFormated
import com.example.weathermaster.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    //val listCityAndWeather: Flow<List<CityAndWeatherFormated>> = repository.listCityAndWeather

    val addCityResult: StateFlow<Boolean> = repository.addCityResult
    suspend fun loadCityList(): List<CityAndWeatherFormated> = repository.listCityAndWeather.first()

    fun deleteCity(city: CityAndWeatherFormated) {
        repository.deleteCity(city)
    }

    fun updateCityList(list: List<CityAndWeatherFormated>) {
        repository.updateCityList(list)
    }

}