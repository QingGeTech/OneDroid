package cn.recommender.androiddevtoolbox.data.local.sys

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.GET_CONFIGURATIONS
import android.content.pm.PackageManager.GET_GIDS
import android.content.pm.PackageManager.GET_INTENT_FILTERS
import android.content.pm.PackageManager.GET_META_DATA
import android.content.pm.PackageManager.GET_PERMISSIONS
import android.content.pm.PackageManager.GET_PROVIDERS
import android.content.pm.PackageManager.GET_RECEIVERS
import android.content.pm.PackageManager.GET_SERVICES
import android.content.pm.PackageManager.GET_SHARED_LIBRARY_FILES
import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
import android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES
import android.os.Build
import androidx.core.content.PackageManagerCompat
import cn.recommender.androiddevtoolbox.util.LogUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.streams.toList

class SysApiImpl @Inject constructor(private val appContext: Application) : SysApi {
//    override fun getAllApps(): List<AppData> {
//        val installedPackages =
//            appContext.packageManager.getInstalledPackages(
//                GET_ACTIVITIES or
//                        GET_SERVICES or
//                        GET_CONFIGURATIONS or
//                        GET_META_DATA or
//                        GET_PERMISSIONS or
//                        GET_PROVIDERS or
//                        GET_RECEIVERS or
//                        GET_SHARED_LIBRARY_FILES or
//                        GET_SIGNATURES or
//                        GET_SIGNING_CERTIFICATES
//            )
//        val appDataList = installedPackages.map { info ->
//            info.
//            AppData(
//                info.applicationInfo.loadIcon(appContext.packageManager),
//                appContext.packageManager.getApplicationLabel(info.applicationInfo).toString(),
//                info.packageName,
//                info.versionCode,
//                info.versionName
//            )
//        }.toList()
//        LogUtil.d("appDataList:$appDataList")
//        return appDataList
//
//    }

    override fun getPkgInfoList(): List<PackageInfo> {
        val installedPackages = appContext.packageManager.getInstalledPackages(commonFlags())
        return installedPackages
    }

    override fun getPackageInfo(packageName: String): PackageInfo {
        return appContext.packageManager.getPackageInfo(packageName, commonFlags())
    }

    private fun commonFlags(): Int {
        var flags = GET_ACTIVITIES or
                GET_SERVICES or
                GET_CONFIGURATIONS or
                GET_META_DATA or
                GET_PERMISSIONS or
                GET_PROVIDERS or
                GET_RECEIVERS or
                GET_SHARED_LIBRARY_FILES or
                GET_GIDS

        flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            flags or GET_SIGNING_CERTIFICATES
        } else {
            flags or GET_SIGNATURES
        }
        return flags
    }
}