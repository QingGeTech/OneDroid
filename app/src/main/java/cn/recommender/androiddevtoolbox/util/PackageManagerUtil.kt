package cn.recommender.androiddevtoolbox.util

import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable

object PackageManagerUtil {
    fun getAppName(packageInfo: PackageInfo, context: Context): String {
        return context.packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
    }

    fun getAppIcon(packageInfo: PackageInfo, context: Context): Drawable {
        return packageInfo.applicationInfo.loadIcon(context.packageManager)
    }
}