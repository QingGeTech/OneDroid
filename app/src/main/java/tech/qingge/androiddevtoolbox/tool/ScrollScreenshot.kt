package tech.qingge.androiddevtoolbox.tool

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
import androidx.core.graphics.createBitmap
import dagger.hilt.android.scopes.ServiceScoped
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.android.Utils.bitmapToMat
import org.opencv.core.Mat
import org.opencv.core.Size
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.databinding.FloatingScrollScreenshotBinding
import tech.qingge.androiddevtoolbox.ui.activity.ScrollScreenshotResultActivity
import tech.qingge.androiddevtoolbox.ui.view.FloatingFrameLayout
import tech.qingge.androiddevtoolbox.util.BitmapUtil
import tech.qingge.androiddevtoolbox.util.DeviceUtil
import tech.qingge.androiddevtoolbox.util.MediaUtil
import tech.qingge.androiddevtoolbox.util.RandomUtil
import tech.qingge.androiddevtoolbox.util.ViewUtil
import java.io.File
import javax.inject.Inject

@ServiceScoped
class ScrollScreenshot @Inject constructor(val appContext: Application) :
    BaseMediaProjectionTool() {

    private val bitmaps: MutableList<Bitmap> = mutableListOf()

    lateinit var binding: FloatingScrollScreenshotBinding

    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var imageReader: ImageReader

    private var isProcessing = false

    private fun start() {
        bitmaps.clear()
        binding.root.text = appContext.getString(R.string.end)
        isProcessing = true
//        screenshot()
    }

    private fun screenshot() {
        binding.root.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            val image = imageReader.acquireLatestImage()
            val bitmap = MediaUtil.imageToBitmap(image)
            image.close()
            binding.root.visibility = View.VISIBLE
            bitmaps.add(bitmap)
        }, 100)
    }

    private fun stop() {
        screenshot()
        Handler(Looper.getMainLooper()).postDelayed({
            isProcessing = false
            binding.root.text = appContext.getString(R.string.start)
            val filePath = compositeBitmaps()
            val intent = Intent(appContext, ScrollScreenshotResultActivity::class.java)
            intent.putExtra("filePath", filePath)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            appContext.startActivity(intent)
        }, 100)
    }

    private fun compositeBitmaps(): String {
        OpenCVLoader.initLocal()
        //TODO: 合并截屏图片，去除重复部分

        // 将每个 Bitmap 转换为 Mat 并计算总高度
        val mats = bitmaps.map {
            val mat = Mat()
            bitmapToMat(it, mat)
            return@map mat
        }
        val totalHeight = mats.sumOf { it.rows() }
        val maxWidth = mats.maxOf { it.cols() }

        // 创建一个大的 Mat 来存储合并后的图像
        val result = Mat(Size(maxWidth.toDouble(), totalHeight.toDouble()), mats[0].type())

        // 将每个 Mat 复制到结果 Mat 中
        var currentY = 0
        for (mat in mats) {
            val roi = result.rowRange(currentY, currentY + mat.rows()).colRange(0, mat.cols())
            mat.copyTo(roi)
            currentY += mat.rows()
        }

        // 将结果 Mat 转换为 Bitmap
        val mergedBitmap = createBitmap(result.cols(), result.rows(), bitmaps[0].config!!)
        Utils.matToBitmap(result, mergedBitmap)

        return saveBitmapToFile(mergedBitmap)
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

    override fun onServiceConnected() {
        imageReader = ImageReader.newInstance(
            DeviceUtil.getScreenWidth(appContext),
            DeviceUtil.getScreenHeight(appContext),
            PixelFormat.RGBA_8888,
            1
        )
        virtualDisplay = mediaProjectionService.createVirtualDisplay(imageReader.surface)
        Handler(Looper.getMainLooper()).postDelayed({
            start()
        }, 300)
    }

    override fun init(ffl: FloatingFrameLayout) {
        binding = FloatingScrollScreenshotBinding.inflate(LayoutInflater.from(ffl.context))
        ffl.addView(binding.root)
        ffl.onTouchOutside = {
            if (isProcessing) {
                screenshot()
            }
        }
        val color = ViewUtil.getColorByStyledAttr(
            ffl.context, R.attr.colorPrimaryContainer
        )
        binding.root.elevation = 0f
        binding.root.backgroundTintList = ColorStateList.valueOf(color)
        binding.root.stateListAnimator = null

        binding.root.setOnClickListener {

            if (isProcessing) {
                stop()
            } else {
                if (this::virtualDisplay.isInitialized) {
                    start()
                } else {
                    initMediaProjectionService(appContext)
                }
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


}