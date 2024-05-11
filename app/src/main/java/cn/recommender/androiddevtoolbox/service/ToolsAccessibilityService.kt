package cn.recommender.androiddevtoolbox.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import cn.recommender.androiddevtoolbox.util.LogUtil

class ToolsAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        LogUtil.d("onAccessibilityEvent:$event")
    }

    override fun onInterrupt() {
        LogUtil.d("onInterrupt")
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil.d("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d("onDestroy")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LogUtil.d("onUnbind")
        return super.onUnbind(intent)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtil.d("onServiceConnected")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

}