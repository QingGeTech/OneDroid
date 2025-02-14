package tech.qingge.androiddevtoolbox.service

import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.view.Surface
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.core.content.IntentCompat
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.Constants
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseForegroundService
import tech.qingge.androiddevtoolbox.util.DeviceUtil
import tech.qingge.androiddevtoolbox.util.LogUtil
import javax.inject.Inject


@AndroidEntryPoint
class MediaProjectionService : BaseForegroundService() {


    inner class Binder : android.os.Binder() {
        fun getService(): MediaProjectionService = this@MediaProjectionService
    }

    @Inject
    lateinit var mediaProjectionManager: MediaProjectionManager

    @Inject
    lateinit var windowManager: WindowManager

    private lateinit var mediaProjection: MediaProjection

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
            )

        mediaProjection.registerCallback(object : MediaProjection.Callback() {
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
        }, null)

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
        )
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


}