package tech.qingge.onedroid.tool

import android.app.Application
import android.content.Intent
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.parcelize.Parcelize
import tech.qingge.onedroid.service.ToolsAccessibilityService
import tech.qingge.onedroid.ui.activity.LayoutInspectActivity
import javax.inject.Inject

@Parcelize
data class ViewNode(
    val className: String,
    val bounds: Rect,
    val resourceId: String?,
    val text: String?,
    val contentDescription: String?,
    val clickable: Boolean,
    val children: MutableList<ViewNode> = mutableListOf()
) : Parcelable

@ServiceScoped
class LayoutInspectTool @Inject constructor(val appContext: Application) :
    BaseScreenshotTool() {

    override fun start(fab: View){
        this.fab = fab
        if (!ToolsAccessibilityService.checkSelfRunning(appContext)){
            return
        }
        applyPermission(fab.context)
    }


    override fun onScreenRecordStart() {
        fab.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            val screenshotPath = screenshot(appContext)

            // 2. 生成 JSON 文件路径
            val uiJsonPath = "${appContext.externalCacheDir?.absolutePath}/ui_dump_${System.currentTimeMillis()}.json"

            // 3. 执行 Dump
            val success = ToolsAccessibilityService.instance?.dumpUiToJson(uiJsonPath) ?: false

            fab.visibility = View.VISIBLE
            val intent = Intent(appContext, LayoutInspectActivity::class.java)
            intent.putExtra("screenshotPath", screenshotPath)
            intent.putExtra("uiJsonPath", uiJsonPath)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            appContext.startActivity(intent)
            stopScreenRecord(appContext)
        }, 500)
    }



}