package cn.recommender.androiddevtoolbox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.data.entity.AppData
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    private val sysApi: SysApi
) : ViewModel() {

    private val _appList: MutableLiveData<List<AppData>> = MutableLiveData()
    val appList: LiveData<List<AppData>> get() = _appList

    fun loadAppData() {
        _appList.value = sysApi.getAppList()
    }
}