package com.example.weathermaster.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.weathermaster.utils.KeyConstants.IMAGE_EXTENSION
import com.example.weathermaster.utils.KeyConstants.IMAGE_URL

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("showIcon")
    fun showIcon(view: ImageView, icon: String?) {
        icon?.let {
            val image = IMAGE_URL + icon + IMAGE_EXTENSION
            Glide.with(view)
                .load(image)
                .into(view)
        }
    }
}