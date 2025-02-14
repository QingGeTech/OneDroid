package tech.qingge.androiddevtoolbox.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.ui.dialog.Dialogs
import tech.qingge.androiddevtoolbox.util.AccessibilityUtil
import tech.qingge.androiddevtoolbox.util.IntentUtil
import tech.qingge.androiddevtoolbox.util.LogUtil

class ToolsAccessibilityService : AccessibilityService() {

    companion object {
        fun checkSelfRunning(context: Context): Boolean {
            if (AccessibilityUtil.isAccessibilityServiceEnabled(
                    context,
                    ToolsAccessibilityService::class.java
                )
            ) {
                return true
            }

            Dialogs.showMessageTips(
                context,
                context.getString(R.string.open_accessibility_service),
//                true
            ) { _, _ ->
                IntentUtil.openAccessibilityServiceSetting(context)
            }
            return false
        }
    }

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