package com.example.weathermaster.settings

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weathermaster.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Singleton
class AppSettings(
    private val applicationContext: Context
) {

    private val _measurement = MutableStateFlow(0)
    val measurement: StateFlow<Int> = _measurement.asStateFlow()

    private val _isPermission = MutableStateFlow(true)
    val isPermission: StateFlow<Boolean> = _isPermission.asStateFlow()

    private val _isStartedApp = MutableStateFlow(false)
    val isStartedApp: StateFlow<Boolean> = _isStartedApp.asStateFlow()

    private var sPref: SharedPreferences = applicationContext.getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)

    private val _isLoadedPreferences = MutableStateFlow(false)
    val isLoadedPreferences: StateFlow<Boolean> = _isLoadedPreferences.asStateFlow()

    init {
        getPreferences()
    }

    fun setPermission(permission: Boolean) {
        _isPermission.value = permission
    }

    fun setIsStarted(isStarted: Boolean) {
        _isStartedApp.value = isStarted
    }

    private fun getPreferences() {
        _isLoadedPreferences.value = false
        _measurement.value = sPref.getInt("measurement", 2)
        _isLoadedPreferences.value = true
    }

    fun savePreferences(
        measurement: Int
    ) {
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putInt("measurement", measurement)
        ed.apply()
        Toast.makeText(applicationContext, applicationContext.getString(R.string.settings_is_saved), Toast.LENGTH_SHORT).show()
        getPreferences()
    }

}