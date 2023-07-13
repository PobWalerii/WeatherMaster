package com.example.weathermaster.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View

object AnimSubstitution {

    fun startAnim(old: View, new: View) {
        val fadeOut = ObjectAnimator.ofFloat(old, "alpha", 1f, 0f)
        fadeOut.duration = 300
        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                old.visibility = View.GONE
                new.visibility = View.VISIBLE
                val fadeIn = ObjectAnimator.ofFloat(new, "alpha", 0f, 1f)
                fadeIn.duration = 300
                fadeIn.start()
            }
        })
        fadeOut.start()
    }
}