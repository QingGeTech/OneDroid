package tech.qingge.onedroid.util

import android.app.ActivityManager
import android.content.Context

object ServiceUtil {

    fun isServiceRunning(context: Context, serviceName: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = am.getRunningServices(30)
        return runningServices.any { it.service.className.contains(serviceName) }
    }

}