package tech.qingge.onedroid.tool

import android.app.Application
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import dagger.hilt.android.scopes.ServiceScoped
import tech.qingge.onedroid.R
import tech.qingge.onedroid.databinding.FloatingLayoutInspectBinding
import tech.qingge.onedroid.ui.activity.LayoutInspectActivity
import tech.qingge.onedroid.ui.view.DraggableFrameLayout
import tech.qingge.onedroid.util.BitmapUtil
import tech.qingge.onedroid.util.DeviceUtil
import tech.qingge.onedroid.util.MediaUtil
import tech.qingge.onedroid.util.RandomUtil
import tech.qingge.onedroid.util.ViewUtil
import java.io.File
import javax.inject.Inject

@ServiceScoped
class LayoutInspect @Inject constructor(val appContext: Application) :
    BaseMediaProjectionTool() {

    lateinit var binding: FloatingLayoutInspectBinding

    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var imageReader: ImageReader

    override fun onServiceConnected() {
        imageReader = ImageReader.newInstance(
            DeviceUtil.getScreenWidth(appContext),
            DeviceUtil.getScreenHeight(appContext),
            PixelFormat.RGBA_8888, 1
        )
        virtualDisplay = mediaProjectionService.createVirtualDisplay(imageReader.surface)
        Handler(Looper.getMainLooper()).postDelayed({
            screenshot()
        }, 300)
    }

    override fun init(ffl: DraggableFrameLayout) {
        binding = FloatingLayoutInspectBinding.inflate(LayoutInflater.from(ffl.context))
        ffl.addView(binding.root)
        val color = ViewUtil.getColorByStyledAttr(
            ffl.context,
            R.attr.colorPrimaryContainer
        )
        binding.root.backgroundTintList = ColorStateList.valueOf(color)
        binding.root.stateListAnimator = null

        binding.root.setOnClickListener {
            if (this::virtualDisplay.isInitialized) {
                screenshot()
            } else {
                initMediaProjectionService(appContext)
            }
        }
    }

    override fun deInit() {
        super.stopMediaProjectionService(appContext)
        if (this::imageReader.isInitialized) {
            imageReader.close()
        }
        if (this::virtualDisplay.isInitialized) {
            virtualDisplay.release()
        }
    }


    private fun screenshot() {
        binding.root.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            val image = imageReader.acquireLatestImage()
            val bitmap = MediaUtil.imageToBitmap(image)
            image.close()

            // 获取uiDump
            val uiFilePath = getUiDump()

            binding.root.visibility = View.VISIBLE

            val filePath = saveBitmapToFile(bitmap)

            val intent = Intent(appContext, LayoutInspectActivity::class.java)
            intent.putExtra("filePath", filePath)
            intent.putExtra("uiFilePath", uiFilePath)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
        }, 200)
    }

    private fun getUiDump(): String {
        // TODO:这样没用。需要使用无障碍服务
        return ""
//        val uiFilePath =
//            appContext.externalCacheDir!!.absolutePath + "/dump_" + RandomUtil.uuid() + ".xml"
//        val p = Runtime.getRuntime().exec("su uiautomator dump $uiFilePath")
//        val content = p.inputStream.bufferedReader().readText()
//        LogUtil.d(content)
//        return if (p.waitFor() == 0) {
//            uiFilePath
//        } else {
//            getUiDump()
//        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String {
        val dir = File("${appContext.externalCacheDir.toString()}/Screenshot")
        if (!dir.exists()) {
            dir.mkdir()
        }

        val filePath = "$dir/${RandomUtil.uuid()}.png"
        BitmapUtil.saveBitmapAsPng(bitmap, 100, filePath)
        return filePath
    }


}