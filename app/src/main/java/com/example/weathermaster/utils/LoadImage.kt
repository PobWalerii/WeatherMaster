package com.example.weathermaster.utils

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide

object LoadImage {

    fun loadImageFromUrl(imageUrl: String, context: Context): Bitmap? {
        try {
            return Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            return null
        }
    }
}