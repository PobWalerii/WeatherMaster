package com.example.weathermaster.settings

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weathermaster.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Singleton

@Singleton
class AppSettings(
    private val applicationContext: Context
) {

    private val _measurement = MutableStateFlow(0)
    val measurement: StateFlow<Int> = _measurement.asStateFlow()


    //private val _tempSimbol = MutableStateFlow("K")
    //val tempSimbol: StateFlow<String> = _tempSimbol.asStateFlow()
    //private val _pressureSimbol = MutableStateFlow(" hPa")
    //val pressureSimbol: StateFlow<String> = _pressureSimbol.asStateFlow()
    //private val _speedSimbol = MutableStateFlow("m/s")
    //val speedSimbol: StateFlow<String> = _speedSimbol.asStateFlow()
    //private val _humiditySimbol = MutableStateFlow(" %")
    //val humiditySimbol: StateFlow<String> = _humiditySimbol.asStateFlow()




    private val _latitude = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> = _longitude.asStateFlow()

    private val _necessaryRefreshForecast = MutableStateFlow(false)
    private val necessaryRefreshForecast: StateFlow<Boolean> = _necessaryRefreshForecast

    //private val _currentRefresh = MutableStateFlow(false)
    //val currentRefresh: StateFlow<Boolean> = _currentRefresh.asStateFlow()

    private val _checkCity = MutableStateFlow(false)
    val checkCity: StateFlow<Boolean> = _checkCity.asStateFlow()

    fun setLocation(latitude: Double, longitude:Double) {
        _latitude.value = latitude
        _longitude.value = longitude
        _checkCity.value = true
        //_currentRefresh.value = true
    }
    //fun setCurrentRefresh() {
    //    _currentRefresh.value = false
    //}
    fun setCheckCity() {
        _checkCity.value = false
    }

    fun setRefreshForecast(value: Boolean) {
        _necessaryRefreshForecast.value = value
    }


    //private val _firstLoad = MutableStateFlow(true)
    //val firstLoad: StateFlow<Boolean> = _firstLoad.asStateFlow()

    //private val _isBackService = MutableStateFlow(false)
    //val isBackService: StateFlow<Boolean> = _isBackService.asStateFlow()

    //private val _isRemoteService = MutableStateFlow(false)
    //val isRemoteService: StateFlow<Boolean> = _isRemoteService.asStateFlow()

    private val _isConnectStatus = MutableStateFlow(true)
    val isConnectStatus: StateFlow<Boolean> = _isConnectStatus.asStateFlow()

    private val _isPermissionStatus = MutableStateFlow(false)
    val isPermissionStatus: StateFlow<Boolean> = _isPermissionStatus.asStateFlow()

    //private val _isDateChanged = MutableStateFlow(false)
    //val isDateChanged: StateFlow<Boolean> = _isDateChanged.asStateFlow()


    //private val _firstRun = MutableStateFlow(SING_OF_FIRST_RUN)
    //val firstRun: StateFlow<Boolean> = _firstRun.asStateFlow()

    //private val _defaultHeader = MutableStateFlow(DEFAULT_HEADER)
    //val defaultHeader: StateFlow<String> = _defaultHeader.asStateFlow()

    //private val _specificationLine = MutableStateFlow(DEFAULT_SPECIFICATION_LINE)
    //val specificationLine: StateFlow<Boolean> = _specificationLine.asStateFlow()

    //private val _defaultAddIfClick = MutableStateFlow(DEFAULT_ADD_IF_CLICK)
    //val defaultAddIfClick: StateFlow<Boolean> = _defaultAddIfClick.asStateFlow()

    //private val _deleteIfSwiped = MutableStateFlow(DELETE_IF_SWIPED)
    //val deleteIfSwiped: StateFlow<Boolean> = _deleteIfSwiped.asStateFlow()

    //private val _dateChanged = MutableStateFlow(DATE_CHANGE_WHEN_CONTENT)
    //val dateChanged: StateFlow<Boolean> = _dateChanged.asStateFlow()

    //private val _showMessageInternetOk = MutableStateFlow(SHOW_MESSAGE_INTERNET_OK)
    //val showMessageInternetOk: StateFlow<Boolean> = _showMessageInternetOk.asStateFlow()

    //private val _startDelayValue = MutableStateFlow(TIME_DELAY_START)
    //val startDelayValue: StateFlow<Int> = _startDelayValue.asStateFlow()

    //private val _queryDelayValue = MutableStateFlow(TIME_DELAY_QUERY)
    //val queryDelayValue: StateFlow<Int> = _queryDelayValue.asStateFlow()

    //private val _requestIntervalValue = MutableStateFlow(INTERVAL_REQUESTS)
    //val requestIntervalValue: StateFlow<Int> = _requestIntervalValue.asStateFlow()

    //private val _operationDelayValue = MutableStateFlow(TIME_DELAY_OPERATION)
    //val operationDelayValue: StateFlow<Int> = _operationDelayValue.asStateFlow()

    //private val _createBackgroundRecords = MutableStateFlow(CREATE_RECORDS_IN_BACKGROUND)
    //val createBackgroundRecords: StateFlow<Boolean> = _createBackgroundRecords.asStateFlow()

    //private val _intervalCreateRecords = MutableStateFlow(INTERVAL_BACKGROUND_CREATE)
    //val intervalCreateRecords: StateFlow<Int> = _intervalCreateRecords.asStateFlow()

    private var sPref: SharedPreferences = applicationContext.getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)

    private val _isLoadedPreferences = MutableStateFlow(false)
    val isLoadedPreferences: StateFlow<Boolean> = _isLoadedPreferences.asStateFlow()

    //var showViewForSnack: View? = null

    fun init() {
        getPreferences()
        //setIsBackService(false)
        //setIsRemoteService(false)
    }

    fun close() {
        //setFirstLoad(true)
    }


    //fun setFirstLoad(isStart: Boolean = false) {
        //_firstLoad.value = isStart
    //}



    //fun setAppFirstRun() {
    //    val ed: SharedPreferences.Editor = sPref.edit()
    //    ed.putBoolean("firstRun", false)
    //    ed.apply()
    //    _firstRun.value = false
    //}

    //fun setFirstLoad(isStart: Boolean = false) {
    //    _firstLoad.value = isStart
    //}

    //fun setIsBackService(state: Boolean) {
    //    _isBackService.value = state
    //}

    //fun setIsRemoteService(state: Boolean) {
    //    _isRemoteService.value = state
    //}

    fun setIsConnectStatus(state: Boolean) {
        _isConnectStatus.value = state
    }

    fun setIsPermissionStatus(state: Boolean) {
        _isPermissionStatus.value = state
    }

    //fun setIsDateChanged(state: Boolean) {
    //    _isDateChanged.value = state
    //}

    private fun getPreferences() {
        _isLoadedPreferences.value = false

        _measurement.value = sPref.getInt("measurement", 1)

        //_firstRun.value = sPref.getBoolean("firstRun", SING_OF_FIRST_RUN)
        //_defaultHeader.value = sPref.getString("defaultHeader", DEFAULT_HEADER).toString()
        //_specificationLine.value =
        //    sPref.getBoolean("specificationLine", DEFAULT_SPECIFICATION_LINE)
        //_defaultAddIfClick.value = sPref.getBoolean("defaultAddIfClick", DEFAULT_ADD_IF_CLICK)
        //_deleteIfSwiped.value = sPref.getBoolean("deleteIfSwiped", DELETE_IF_SWIPED)
        //_dateChanged.value = sPref.getBoolean("dateChanged", DATE_CHANGE_WHEN_CONTENT)
        //_showMessageInternetOk.value =
        //    sPref.getBoolean("showMessageInternetOk", SHOW_MESSAGE_INTERNET_OK)
        //_startDelayValue.value = sPref.getInt("startDelayValue", TIME_DELAY_START)
        //_queryDelayValue.value = sPref.getInt("queryDelayValue", TIME_DELAY_QUERY)
        //_requestIntervalValue.value = sPref.getInt("requestIntervalValue", INTERVAL_REQUESTS)
        //_operationDelayValue.value = sPref.getInt("operationDelayValue", TIME_DELAY_OPERATION)
        //_createBackgroundRecords.value =
        //    sPref.getBoolean("createBackgroundRecords", CREATE_RECORDS_IN_BACKGROUND)
        //_intervalCreateRecords.value =
        //    sPref.getInt("intervalCreateRecords", INTERVAL_BACKGROUND_CREATE)

        _isLoadedPreferences.value = true
    }

    fun savePreferences(
        measurement: Int,

        //firstRun: Boolean,
        //defaultHeader: String,
        //specificationLine: Boolean,
        //defaultAddIfClick: Boolean,
        //deleteIfSwiped: Boolean,
        //dateChanged: Boolean,
        //showMessageInternetOk: Boolean,
        //startDelayValue: Int,
        //queryDelayValue: Int,
        //requestIntervalValue: Int,
        //operationDelayValue: Int,
        //createBackgroundRecords: Boolean,
        //intervalCreateRecords: Int,
        //getDefault: Boolean = false
    ) {
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putInt("measurement", measurement)
        //ed.putBoolean("firstRun", firstRun)
        //ed.putString("defaultHeader", defaultHeader.ifEmpty { DEFAULT_HEADER })
        //ed.putBoolean("specificationLine", specificationLine)
        //ed.putBoolean("defaultAddIfClick", defaultAddIfClick)
        //ed.putBoolean("deleteIfSwiped", deleteIfSwiped)
        //ed.putBoolean("dateChanged", dateChanged)
        //ed.putBoolean("showMessageInternetOk", showMessageInternetOk)
        //ed.putInt("startDelayValue", startDelayValue)
        //ed.putInt("queryDelayValue", queryDelayValue)
        //ed.putInt("requestIntervalValue", if(requestIntervalValue>0) requestIntervalValue else INTERVAL_REQUESTS)
        //ed.putInt("operationDelayValue", operationDelayValue)
        //ed.putBoolean("createBackgroundRecords", createBackgroundRecords)
        //ed.putInt("intervalCreateRecords", if(intervalCreateRecords>=MIN_INTERVAL_BACKGROUND_CREATE) intervalCreateRecords else MIN_INTERVAL_BACKGROUND_CREATE)
        ed.apply()
        Toast.makeText(
            applicationContext, applicationContext.getString(R.string.settings_is_saved), Toast.LENGTH_SHORT
        ).show()
        getPreferences()
    }

}