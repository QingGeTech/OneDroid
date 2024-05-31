package cn.recommender.androiddevtoolbox.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
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
        _themeColor.value = spApi.getThemeColor()
    }

    fun onDarkThemeChange(isDarkTheme: Boolean) {
        spApi.setDarkTheme(isDarkTheme)
        _isDarkTheme.value = isDarkTheme
        // delay 100ms for smooth animation
        viewModelScope.launch {
            delay(100)
            if (isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    fun onThemeColorChange(color: Int) {
        spApi.setThemeColor(color)
        _themeColor.value = color
    }

}