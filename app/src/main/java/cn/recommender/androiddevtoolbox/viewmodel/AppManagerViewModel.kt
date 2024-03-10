package cn.recommender.androiddevtoolbox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.data.entity.AppData

class AppManagerViewModel : ViewModel() {

    private val _appList: MutableLiveData<List<AppData>> = MutableLiveData()
    val appList: LiveData<List<AppData>> get() = _appList

    fun loadAppData() {
        _appList.value = App.sys.getAppList()
    }
}