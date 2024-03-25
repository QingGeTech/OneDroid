package cn.recommender.androiddevtoolbox.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.data.entity.AppData
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor(
    private val spApi: SpApi,
    private val application: Application
) : ViewModel() {

    private val _themeColor: MutableLiveData<Int> = MutableLiveData()
    val themeColor: LiveData<Int> get() = _themeColor

    private val _isDarkTheme: MutableLiveData<Boolean> = MutableLiveData()
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    fun loadSettings() {
        loadThemeColor()
        loadDarkTheme()
    }

    private fun loadDarkTheme() {
        _isDarkTheme.value = spApi.isDarkTheme()
    }

    private fun loadThemeColor() {
        val obtainStyledAttributes =
            application.obtainStyledAttributes(intArrayOf(R.attr.colorPrimary))
        _themeColor.value = obtainStyledAttributes.getColor(0, 0)
        obtainStyledAttributes.recycle()
    }

    fun onDarkThemeChange(isDarkTheme: Boolean) {
        spApi.setDarkTheme(isDarkTheme)
        _isDarkTheme.value = isDarkTheme
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}