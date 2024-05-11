package cn.recommender.androiddevtoolbox.tool

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
import cn.recommender.androiddevtoolbox.util.LogUtil
import javax.inject.Inject

class ScreenRecorder @Inject constructor(private val appContext: Application) {

    fun start() {
        LogUtil.d("start screen record")
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }
}