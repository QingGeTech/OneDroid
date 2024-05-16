package cn.recommender.androiddevtoolbox.tool

import android.app.Application
import android.content.Intent
import android.provider.Settings
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.util.LogUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenRecord @Inject constructor(val appContext: Application) {


    fun start() {
        LogUtil.d("start screen record")
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }
}