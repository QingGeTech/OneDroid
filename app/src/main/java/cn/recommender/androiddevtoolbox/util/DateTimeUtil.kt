package cn.recommender.androiddevtoolbox.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object DateTimeUtil {
    @SuppressLint("ConstantLocale")
    private val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    fun getFormattedDateTime(timestamp: Long): String {
        return formatter.format(Date(timestamp))
    }
}