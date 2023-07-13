package com.example.weathermaster.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkStatus {

    fun connectStatus(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork != null
    }

}