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

    private val _isConnectStatus = MutableStateFlow(false)
    val isConnectStatus: StateFlow<Boolean> = _isConnectStatus.asStateFlow()

    //private val _isServiceStatus = MutableStateFlow(false)
    //val isServiceStatus: StateFlow<Boolean> = _isServiceStatus.asStateFlow()

    private var sPref: SharedPreferences = applicationContext.getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)

    private val _isLoadedPreferences = MutableStateFlow(false)
    val isLoadedPreferences: StateFlow<Boolean> = _isLoadedPreferences.asStateFlow()

    fun init() {
        getPreferences()
    }

    fun close() {
    }

    fun setIsConnectStatus(state: Boolean) {
        _isConnectStatus.value = state
    }

    //fun setIsServiceStatus() {
    //    _isServiceStatus.value = true
    //}

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