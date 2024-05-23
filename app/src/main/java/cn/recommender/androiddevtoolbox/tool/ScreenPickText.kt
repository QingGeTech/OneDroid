package cn.recommender.androiddevtoolbox.tool

import android.app.Application
import android.content.Intent
import android.provider.Settings
import cn.recommender.androiddevtoolbox.util.LogUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenPickText @Inject constructor(val appContext: Application) {

    fun start() {
        LogUtil.d("start screen pick text")
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }
}