package cn.recommender.androiddevtoolbox.data.local.sys

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES
import android.os.Build
import cn.recommender.androiddevtoolbox.data.entity.AppData
import cn.recommender.androiddevtoolbox.util.LogUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.streams.toList

class SysApiImpl @Inject constructor(private val appContext: Application) : SysApi {
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
            LogUtil.d("appDataList:$appDataList")
            return appDataList
        }else{
            //TODO
        }
        return emptyList()

    }
}