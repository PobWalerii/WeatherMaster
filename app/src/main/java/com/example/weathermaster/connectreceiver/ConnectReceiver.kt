package com.example.weathermaster.connectreceiver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.widget.Toast
import com.example.weathermaster.R
import com.example.weathermaster.settings.AppSettings
import javax.inject.Singleton

@Singleton
class ConnectReceiver(
    private val appSettings: AppSettings,
    private val applicationContext: Context,
) {

    private val connectivityManager: ConnectivityManager =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            setConnectStatus(true)
        }
        override fun onLost(network: Network) {
            setConnectStatus(false)
        }
    }

    fun init() {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback
        )
        setConnectStatus(connectivityManager.activeNetwork != null)
    }
    fun close() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    internal fun setConnectStatus(status: Boolean) {
        showStatus(status)
        appSettings.setIsConnectStatus(status)
    }

    private fun showStatus(
        isConnect: Boolean,
    ) {
        if (!isConnect) {
            Toast.makeText(applicationContext, R.string.text_no_internet, Toast.LENGTH_SHORT)
                .show()
        }
    }

}
