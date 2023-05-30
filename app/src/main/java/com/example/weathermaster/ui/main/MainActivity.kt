package com.example.weathermaster.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weathermaster.R
import com.example.weathermaster.utils.SplashScreen.startSplash
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        startSplash(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}