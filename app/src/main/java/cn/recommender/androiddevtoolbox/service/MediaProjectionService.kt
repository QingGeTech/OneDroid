package cn.recommender.androiddevtoolbox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.DisplayCompat
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseService
import cn.recommender.androiddevtoolbox.util.BitmapUtil
import cn.recommender.androiddevtoolbox.util.DeviceUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.RandomUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MediaProjectionService : BaseService() {


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
        return Binder()
    }


    override fun onCreate() {
        super.onCreate()
        makeForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaProjection =
            mediaProjectionManager.getMediaProjection(
                intent!!.getIntExtra("resultCode", 0),
                intent.getParcelableExtra("data")!!
            )
        return START_NOT_STICKY
    }

    private fun makeForeground() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("100", "MediaProjection", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "100")
            .setContentTitle("MediaProjectionService")
            .setContentText("MediaProjectionService is running")
            .setSmallIcon(R.drawable.app_logo_round)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    baseContext.resources,
                    R.drawable.app_logo_round
                )
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(1, notification)
    }

    fun screenshot(onResult: (Bitmap, String) -> Unit) {
        val width = DeviceUtil.getScreenWidth(this)
        val height = DeviceUtil.getScreenHeight(this)
        val density = DeviceUtil.getScreenDensityDpi(this)

        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        val surface = imageReader.surface
        val virtualDisplay = mediaProjection.createVirtualDisplay(
            "Screenshot", width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, surface, null, null
        )
        imageReader.setOnImageAvailableListener({
            val image: Image = imageReader.acquireLatestImage()
            virtualDisplay.release()

            val planes = image.planes;
            val buffer = planes[0].buffer;
            val pixelStride = planes[0].pixelStride;
            val rowStride = planes[0].rowStride;
            val rowPadding = rowStride - pixelStride * width;

            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            );
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()

            val dir = File("${externalCacheDir.toString()}/screenshot")
            if (!dir.exists()) {
                dir.mkdir()
            }

            val filePath = "$dir/${RandomUtil.uuid()}.png"
            BitmapUtil.saveBitmapAsPng(bitmap, 100, filePath)

            onResult(bitmap, filePath)
        }, null)

    }
}