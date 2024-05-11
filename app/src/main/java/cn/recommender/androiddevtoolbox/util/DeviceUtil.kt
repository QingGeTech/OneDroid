package cn.recommender.androiddevtoolbox.util

import android.content.Context
import android.view.WindowManager
import androidx.core.content.ContextCompat

object DeviceUtil {
    fun getScreenWidth(context: Context): Int {
        val display = ContextCompat.getDisplayOrDefault(context)
        return display.width
    }

    fun getScreenHeight(context: Context): Int {
        val display = ContextCompat.getDisplayOrDefault(context)
        return display.height
    }
}