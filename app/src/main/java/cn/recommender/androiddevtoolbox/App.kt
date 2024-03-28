package cn.recommender.androiddevtoolbox

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.util.LogUtil
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import com.google.android.material.color.utilities.MaterialDynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var spApi: SpApi

    override fun onCreate() {
        super.onCreate()
        addActivityLifecycleObserver()
        initDarkMode()
    }

    private fun initDarkMode() {
        if (spApi.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun addActivityLifecycleObserver() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                LogUtil.d("activity:${activity.javaClass.name} : onCreate")
            }

            override fun onActivityStarted(activity: Activity) {
                LogUtil.d("activity:${activity.javaClass.name} : onStart")
            }

            override fun onActivityResumed(activity: Activity) {
                LogUtil.d("activity:${activity.javaClass.name} : onResume")
            }

            override fun onActivityPaused(activity: Activity) {
                LogUtil.d("activity:${activity.javaClass.name} : onPause")
            }

            override fun onActivityStopped(activity: Activity) {
                LogUtil.d("activity:${activity.javaClass.name} : onStop")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                LogUtil.d("activity:${activity.javaClass.name} : onSaveInstanceState")
            }

            override fun onActivityDestroyed(activity: Activity) {
                LogUtil.d("activity:${activity.javaClass.name} : onDestroy")
            }

        })
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        LogUtil.d("attachBaseContext")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        LogUtil.d("onLowMemory")
    }

}