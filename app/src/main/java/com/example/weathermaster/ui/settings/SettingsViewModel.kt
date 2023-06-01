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
/*
    val firstRun: StateFlow<Boolean> = appSettings.firstRun
    val defaultHeader: StateFlow<String> = appSettings.defaultHeader
    val specificationLine: StateFlow<Boolean> = appSettings.specificationLine
    val defaultAddIfClick: StateFlow<Boolean> = appSettings.defaultAddIfClick
    val deleteIfSwiped: StateFlow<Boolean> = appSettings.deleteIfSwiped
    val dateChanged: StateFlow<Boolean> = appSettings.dateChanged
    val showMessageInternetOk: StateFlow<Boolean> = appSettings.showMessageInternetOk
    val startDelayValue: StateFlow<Int> = appSettings.startDelayValue
    val queryDelayValue: StateFlow<Int> = appSettings.queryDelayValue
    val requestIntervalValue: StateFlow<Int> = appSettings.requestIntervalValue
    val operationDelayValue: StateFlow<Int> = appSettings.operationDelayValue
    val createBackgroundRecords: StateFlow<Boolean> = appSettings.createBackgroundRecords
    val intervalCreateRecords: StateFlow<Int> = appSettings.intervalCreateRecords

 */

    var measurement: StateFlow<Int> = appSettings.measurement

    var isLoadedPreferences: StateFlow<Boolean> = appSettings.isLoadedPreferences

    //init {
    //    appSettings.showViewForSnack = null
    //}


    fun savePreferences(
        measurement: Int,

        /*
        firstRun: Boolean,
        defaultHeader: String,
        specificationLine: Boolean,
        defaultAddIfClick: Boolean,
        deleteIfSwiped: Boolean,
        dateChanged: Boolean,
        showMessageInternetOk: Boolean,
        startDelayValue: Int,
        queryDelayValue: Int,
        requestIntervalValue: Int,
        operationDelayValue: Int,
        createBackgroundRecords: Boolean,
        intervalCreateRecords: Int
         */
    ) {
        appSettings.savePreferences(
            measurement
            /*
            firstRun,
            defaultHeader,
            specificationLine,
            defaultAddIfClick,
            deleteIfSwiped,
            dateChanged,
            showMessageInternetOk,
            startDelayValue,
            queryDelayValue,
            requestIntervalValue,
            operationDelayValue,
            createBackgroundRecords,
            intervalCreateRecords
             */
        )
    }

    fun isChange(
        _measurement: Int,

        /*
        _firstRun: Boolean,
        _defaultHeader: String,
        _specificationLine: Boolean,
        _defaultAddIfClick: Boolean,
        _deleteIfSwiped: Boolean,
        _dateChanged: Boolean,
        _showMessageInternetOk: Boolean,
        _startDelayValue: String,
        _queryDelayValue: String,
        _requestIntervalValue: String,
        _operationDelayValue: String,
        _createBackgroundRecords: Boolean,
        _intervalCreateRecords: String

         */
    ): Boolean {
        return measurement.value != _measurement
        /*
        return  firstRun.value != _firstRun ||
                defaultHeader.value != _defaultHeader ||
                specificationLine.value != _specificationLine ||
                defaultAddIfClick.value != _defaultAddIfClick ||
                deleteIfSwiped.value != _deleteIfSwiped ||
                dateChanged.value != _dateChanged ||
                showMessageInternetOk.value != _showMessageInternetOk ||
                startDelayValue.value.toString() != _startDelayValue ||
                queryDelayValue.value.toString() != _queryDelayValue ||
                requestIntervalValue.value.toString() != _requestIntervalValue ||
                operationDelayValue.value.toString() != _operationDelayValue ||
                createBackgroundRecords.value != _createBackgroundRecords ||
                intervalCreateRecords.value.toString() != _intervalCreateRecords
         */
    }
}