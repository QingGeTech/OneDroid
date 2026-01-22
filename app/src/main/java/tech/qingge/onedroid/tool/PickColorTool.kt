package tech.qingge.onedroid.tool

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import dagger.hilt.android.scopes.ServiceScoped
import tech.qingge.onedroid.ui.activity.PickColorActivity
import javax.inject.Inject

@ServiceScoped
class PickColorTool @Inject constructor(val appContext: Application) : BaseScreenshotTool() {

    override fun onScreenRecordStart() {
        fab.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            val filePath = screenshot(appContext)
            fab.visibility = View.VISIBLE
            val intent = Intent(appContext, PickColorActivity::class.java)
            intent.putExtra("filePath", filePath)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            appContext.startActivity(intent)
            stopScreenRecord(appContext)
        }, 500)
    }

}