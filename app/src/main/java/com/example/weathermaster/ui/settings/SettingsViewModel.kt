package com.example.weathermaster.ui.settings

import androidx.lifecycle.ViewModel
import com.example.weathermaster.settings.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettings: AppSettings
): ViewModel() {

    var measurement: StateFlow<Int> = appSettings.measurement

    var isLoadedPreferences: StateFlow<Boolean> = appSettings.isLoadedPreferences

    fun savePreferences(measurement: Int) {
        appSettings.savePreferences(
            measurement
        )
    }

    fun isChange(_measurement: Int): Boolean = measurement.value != _measurement

}