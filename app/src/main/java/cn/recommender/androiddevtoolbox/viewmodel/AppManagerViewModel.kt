package cn.recommender.androiddevtoolbox.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.Constants
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import cn.recommender.androiddevtoolbox.util.PackageManagerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    private val sysApi: SysApi,
    private val spApi: SpApi
) : ViewModel() {

    @Inject
    lateinit var appContext: Application

    private val _appList: MutableLiveData<List<PackageInfo>> = MutableLiveData()
    val appList: LiveData<List<PackageInfo>> get() = _appList

    private var searchAppList: List<PackageInfo>? = null

    fun initAppList() {
        var allPkgs = sysApi.getPkgInfoList()
        when (spApi.getAppFilterType()) {
            Constants.APP_FILTER_TYPE_SYSTEM ->
                allPkgs =
                    allPkgs.filter { packageInfo -> packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 }

            Constants.APP_FILTER_TYPE_USER ->
                allPkgs =
                    allPkgs.filter { packageInfo -> packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
        }
        _appList.value = allPkgs
    }

    fun startSearch() {
        searchAppList = appList.value
    }

    fun filterAppList(keyword: String) {
        if (searchAppList == null){
            return
        }
        if (TextUtils.isEmpty(keyword)) {
            _appList.value = searchAppList!!
            return
        }
        _appList.value = searchAppList!!.filter { packageInfo ->
            val appName = PackageManagerUtil.getAppName(packageInfo, appContext)
            return@filter appName.contains(keyword, true) || packageInfo.packageName.contains(
                keyword, true
            )
        }
    }

    fun stopSearch() {
        _appList.value = searchAppList!!
        searchAppList = null
    }
}