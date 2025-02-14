package tech.qingge.androiddevtoolbox.data.local.sys

import android.content.Context
import android.content.pm.PackageInfo

interface SysApi {
    //    fun getAllApps(): List<AppData>
    fun getPkgInfoList(): List<PackageInfo>

    fun getPackageInfo(packageName: String): PackageInfo

}