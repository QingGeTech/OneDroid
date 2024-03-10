package cn.recommender.androiddevtoolbox.data.local.sys

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES
import android.os.Build
import androidx.annotation.RequiresApi
import cn.recommender.androiddevtoolbox.data.entity.AppData
import kotlin.streams.toList

class SysApiImpl(private val appContext: Context) : SysApi {
    override fun getAppList(): List<AppData> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val installedPackages =
                appContext.packageManager.getInstalledPackages(MATCH_UNINSTALLED_PACKAGES)
            val appDataList = installedPackages.map { info ->
                AppData(
                    info.applicationInfo.loadIcon(appContext.packageManager),
                    appContext.packageManager.getApplicationLabel(info.applicationInfo).toString(),
                    info.packageName,
                    info.versionCode,
                    info.versionName
                )
            }.toList()
            println("appDataList:$appDataList")
            return appDataList
        }
        return emptyList()

    }
}