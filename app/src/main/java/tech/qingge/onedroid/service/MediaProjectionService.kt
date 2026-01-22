package tech.qingge.onedroid.service

import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.view.Surface
import androidx.activity.result.ActivityResult
import androidx.core.content.IntentCompat
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseForegroundService
import tech.qingge.onedroid.util.DeviceUtil
import tech.qingge.onedroid.util.LogUtil
import javax.inject.Inject


@AndroidEntryPoint
class MediaProjectionService : BaseForegroundService() {


    inner class Binder : android.os.Binder() {
        fun getService(): MediaProjectionService = this@MediaProjectionService
    }

    @Inject
    lateinit var mediaProjectionManager: MediaProjectionManager

    private lateinit var mediaProjection: MediaProjection

    val callback = object : MediaProjection.Callback() {
        override fun onCapturedContentResize(width: Int, height: Int) {
            super.onCapturedContentResize(width, height)
            LogUtil.d("onCapturedContentResize:width:$width,height:$height")
        }

        override fun onCapturedContentVisibilityChanged(isVisible: Boolean) {
            super.onCapturedContentVisibilityChanged(isVisible)
            LogUtil.d("onCapturedContentVisibilityChanged:isVisible:$isVisible")
        }

        override fun onStop() {
            super.onStop()
            LogUtil.d("onStop")
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        val activityResult = IntentCompat.getParcelableExtra(
            intent!!,
            "activityResult",
            ActivityResult::class.java
        )!!
        mediaProjection =
            mediaProjectionManager.getMediaProjection(
                activityResult.resultCode,
                activityResult.data!!
            )!!

        mediaProjection.registerCallback(callback, null)

        return Binder()
    }

    fun createVirtualDisplay(surface: Surface): VirtualDisplay {
        return mediaProjection.createVirtualDisplay(
            "virtualDisplay",
            DeviceUtil.getScreenWidth(this),
            DeviceUtil.getScreenHeight(this),
            DeviceUtil.getScreenDensityDpi(this),
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface,
            null,
            null
        )!!
    }

    override fun getNotificationData(): NotificationData {
        return NotificationData(
            Constants.NOTIFICATION_NOTIFY_ID_MEDIA_PROJECTION,
            getString(R.string.app_name), getString(R.string.media_projection_in_process)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    fun stop(){
        mediaProjection.unregisterCallback(callback)
        mediaProjection.stop()

        stopForeground(true)
        stopSelf()
    }


}