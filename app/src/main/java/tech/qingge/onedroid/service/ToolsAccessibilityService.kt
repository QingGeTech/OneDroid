package tech.qingge.onedroid.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.gson.Gson
import tech.qingge.onedroid.R
import tech.qingge.onedroid.tool.ViewNode
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.util.AccessibilityUtil
import tech.qingge.onedroid.util.IntentUtil
import tech.qingge.onedroid.util.LogUtil
import java.io.File

class ToolsAccessibilityService : AccessibilityService() {

    companion object {
        var instance: ToolsAccessibilityService? = null
        private val gson = Gson()
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
        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    // 将 UI 树保存为 JSON 文件并返回路径
    fun dumpUiToJson(savePath: String): Boolean {
        val root = rootInActiveWindow ?: return false
        return try {
            val rootNode = convertNode(root)
            File(savePath).writeText(gson.toJson(rootNode))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            root.recycle()
        }
    }

    private fun convertNode(info: AccessibilityNodeInfo): ViewNode {
        val rect = Rect()
        info.getBoundsInScreen(rect)
        val node = ViewNode(
            className = info.className?.toString() ?: "View",
            bounds = rect,
            resourceId = info.viewIdResourceName,
            text = info.text?.toString(),
            contentDescription = info.contentDescription?.toString(),
            clickable = info.isClickable
        )
        for (i in 0 until info.childCount) {
            info.getChild(i)?.let { child ->
                node.children.add(convertNode(child))
                child.recycle()
            }
        }
        return node
    }


}