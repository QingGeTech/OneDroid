package tech.qingge.onedroid.util

import android.util.Log

/**
 * Log control
 */
object LogUtil {
    fun d(msg: String) {
        Log.d(getFileTag(), msg)
    }


    fun e(msg: String, t: Throwable) {
        Log.e(getFileTag(), msg, t)
    }

    private fun getFileTag(): String {
        val caller = Throwable().fillInStackTrace().stackTrace[3]
        return caller.className
    }
}