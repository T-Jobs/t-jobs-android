package ru.nativespeakers.core.ui

import android.app.Activity
import android.view.View
import androidx.core.view.WindowCompat

fun setStatusBarMode(view: View, isDark: Boolean) {
    val window = (view.context as Activity).window
    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
}