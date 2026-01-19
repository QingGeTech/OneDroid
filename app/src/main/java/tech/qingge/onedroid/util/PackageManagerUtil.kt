package tech.qingge.onedroid.util

import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.os.Build
import tech.qingge.onedroid.R

object PackageManagerUtil {
    fun getAppName(packageInfo: PackageInfo, context: Context): String {
        return context.packageManager.getApplicationLabel(packageInfo.applicationInfo!!).toString()
    }

    fun getAppIcon(packageInfo: PackageInfo, context: Context): Drawable {
        return packageInfo.applicationInfo!!.loadIcon(context.packageManager)
    }

    fun getRevisionCode(packageInfo: PackageInfo, context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            packageInfo.baseRevisionCode.toString()
        } else {
            context.getString(R.string.not_supported)
        }
    }

    fun getCompileSdkVersion(packageInfo: PackageInfo, context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            packageInfo.applicationInfo!!.compileSdkVersion.toString()
        } else {
            context.getString(R.string.not_supported)
        }
    }

    fun getMinSdkVersion(packageInfo: PackageInfo, context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageInfo.applicationInfo!!.minSdkVersion.toString()
        } else {
            context.getString(R.string.not_supported)
        }
    }


}