package tech.qingge.onedroid.tool

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.os.IBinder
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.core.content.IntentCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.R
import tech.qingge.onedroid.service.MediaProjectionService
import tech.qingge.onedroid.ui.activity.MediaProjectionPermissionActivity
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.util.BitmapUtil
import tech.qingge.onedroid.util.DeviceUtil
import tech.qingge.onedroid.util.MediaUtil
import tech.qingge.onedroid.util.RandomUtil
import java.io.File

abstract class BaseMediaProjectionTool {

    var mediaProjectionService: MediaProjectionService? = null
    var serviceConnection: ServiceConnection? = null

    var virtualDisplay: VirtualDisplay? = null
    var imageReader: ImageReader? = null

    lateinit var fab: View

    fun applyPermission(appContext: Context) {
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
        atyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(atyIntent)
    }

    private fun bindService(appContext: Context, activityResult: ActivityResult) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mediaProjectionService =
                    (service as MediaProjectionService.Binder).getService()
                imageReader = ImageReader.newInstance(
                    DeviceUtil.getScreenWidth(appContext),
                    DeviceUtil.getScreenHeight(appContext),
                    PixelFormat.RGBA_8888, 1
                )
                virtualDisplay =
                    mediaProjectionService!!.createVirtualDisplay(imageReader!!.surface)
                onScreenRecordStart()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                serviceConnection = null
                mediaProjectionService = null
            }
        }
        val intent = Intent(appContext, MediaProjectionService::class.java)
        intent.putExtra("activityResult", activityResult)
        appContext.bindService(intent, serviceConnection!!, Service.BIND_AUTO_CREATE)
    }

    fun screenshot(appContext: Context): String {
        val image = imageReader!!.acquireLatestImage()
        val bitmap = MediaUtil.imageToBitmap(image)
        image.close()

        val filePath = saveBitmapToFile(bitmap, appContext)
        return filePath
    }

    fun saveBitmapToFile(bitmap: Bitmap, appContext: Context): String {
        val dir = File("${appContext.externalCacheDir.toString()}/Screenshot")
        if (!dir.exists()) {
            dir.mkdir()
        }

        val filePath = "$dir/${RandomUtil.uuid()}.png"
        BitmapUtil.saveBitmapAsPng(bitmap, 100, filePath)
        return filePath
    }

    fun stopScreenRecord(appContext: Context){
        imageReader?.close()
        imageReader = null

        virtualDisplay?.release()
        virtualDisplay = null

        mediaProjectionService?.stop()
        mediaProjectionService = null

        if (serviceConnection != null) {
            appContext.unbindService(serviceConnection!!)
            serviceConnection = null
        }
        appContext.stopService(Intent(appContext, MediaProjectionService::class.java))

    }

    fun start(fab: View){
        this.fab = fab
        applyPermission(fab.context)
    }

    abstract fun onScreenRecordStart()


}