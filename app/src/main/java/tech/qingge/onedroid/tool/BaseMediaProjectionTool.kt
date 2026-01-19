package tech.qingge.onedroid.tool

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import androidx.activity.result.ActivityResult
import androidx.core.content.IntentCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.R
import tech.qingge.onedroid.service.MediaProjectionService
import tech.qingge.onedroid.ui.activity.MediaProjectionPermissionActivity
import tech.qingge.onedroid.ui.dialog.Dialogs

abstract class BaseMediaProjectionTool : BaseTool() {

    lateinit var mediaProjectionService: MediaProjectionService
    private var serviceConnection: ServiceConnection? = null

    fun stopMediaProjectionService(appContext: Context) {
        if (serviceConnection != null) {
            appContext.unbindService(serviceConnection!!)
            serviceConnection = null
        }
        appContext.stopService(Intent(appContext, MediaProjectionService::class.java))
    }

    fun initMediaProjectionService(appContext: Context) {
        applyPermission(appContext)
    }

    private fun applyPermission(appContext: Context) {
        val intentFilter =
            IntentFilter(Constants.LOCAL_BROADCAST_ACTION_MEDIA_PROJECTION_PERMISSION_RESULT)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null || intent.action != Constants.LOCAL_BROADCAST_ACTION_MEDIA_PROJECTION_PERMISSION_RESULT) {
                    return
                }

                val activityResult = IntentCompat.getParcelableExtra(
                    intent,
                    "activityResult",
                    ActivityResult::class.java
                )!!

                if (activityResult.resultCode == Activity.RESULT_OK) {
                    bindService(appContext, activityResult)
                } else {
                    Dialogs.showMessageTips(
                        appContext, appContext.getString(R.string.permission_not_granted)
                    )
                }
                LocalBroadcastManager.getInstance(appContext)
                    .unregisterReceiver(this)
            }
        }
        LocalBroadcastManager.getInstance(appContext).registerReceiver(receiver, intentFilter)

        val atyIntent = Intent(appContext, MediaProjectionPermissionActivity::class.java)
        atyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        appContext.startActivity(atyIntent)
    }

    private fun bindService(appContext: Context, activityResult: ActivityResult) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mediaProjectionService =
                    (service as MediaProjectionService.Binder).getService()
                this@BaseMediaProjectionTool.onServiceConnected()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
//                serviceConnection = null
//                mediaProjectionService = null
            }
        }
        val intent = Intent(appContext, MediaProjectionService::class.java)
        intent.putExtra("activityResult", activityResult)
        appContext.bindService(intent, serviceConnection!!, Service.BIND_AUTO_CREATE)
    }

    abstract fun onServiceConnected()

}