package tech.qingge.androiddevtoolbox.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object DateTimeUtil {
    @SuppressLint("ConstantLocale")
    private val formatter: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun getFormattedDateTime(timestamp: Long): String {
        return getFormattedDateTime(Date(timestamp))
    }

    fun getFormattedDateTime(date: Date): String {
        return formatter.format(date)
    }
}