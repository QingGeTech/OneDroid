package tech.qingge.androiddevtoolbox.tool

import android.app.Application
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import dagger.hilt.android.scopes.ServiceScoped
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

    fun compositeBitmaps(): String {
        if (bitmaps.isEmpty()) return ""
        if (bitmaps.size == 1) {
            val singlePath = saveBitmapToFile(bitmaps[0])
            return singlePath
        }

        val width = bitmaps[0].width
        val height = bitmaps[0].height

        // 检测固定区域
        val headerHeight = detectHeaderHeight(bitmaps)
        val footerHeight = detectFooterHeight(bitmaps)
        val contentSegments = mutableListOf<Bitmap>()
        var totalContentHeight = 0

        // 处理第一张图的内容区域
        val firstContentHeight = height - headerHeight - footerHeight
        if (firstContentHeight > 0) {
            val firstContent = Bitmap.createBitmap(bitmaps[0], 0, headerHeight, width, firstContentHeight)
            contentSegments.add(firstContent)
            totalContentHeight += firstContentHeight
        }

        // 处理后续图片的非重复内容
        for (i in 1 until bitmaps.size) {
            val prev = bitmaps[i - 1]
            val curr = bitmaps[i]
            val overlapHeight = findOverlapHeight(prev, curr, headerHeight, footerHeight, height - headerHeight - footerHeight)
            val contentHeight = height - headerHeight - footerHeight - overlapHeight

            if (contentHeight > 0) {
                val content = Bitmap.createBitmap(curr, 0, headerHeight + overlapHeight, width, contentHeight)
                contentSegments.add(content)
                totalContentHeight += contentHeight
            }
        }

        // 计算最终高度
        val totalHeight = headerHeight + totalContentHeight + footerHeight
        val resultBitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // 绘制固定上部分（如果存在）
        if (headerHeight > 0) {
            canvas.drawBitmap(bitmaps[0], 0f, 0f, null, 0, 0, width, headerHeight)
        }

        // 绘制所有内容段
        var currentTop = headerHeight
        contentSegments.forEach { segment ->
            canvas.drawBitmap(segment, 0f, currentTop.toFloat(), null)
            currentTop += segment.height
        }

        // 绘制固定下部分（如果存在）
        if (footerHeight > 0) {
            canvas.drawBitmap(
                bitmaps[0],
                0f,
                (totalHeight - footerHeight).toFloat(),
                null,
                0,
                height - footerHeight,
                width,
                footerHeight
            )
        }

        // 保存结果
        val outputPath = saveBitmapToFile(resultBitmap)

        // 清理资源
        contentSegments.forEach { if (!it.isRecycled) it.recycle() }
        if (!resultBitmap.isRecycled) resultBitmap.recycle()

        return outputPath
    }

    // 检测固定上部分高度
    private fun detectHeaderHeight(bitmaps: List<Bitmap>): Int {
        if (bitmaps.size < 2) return 0
        val first = bitmaps[0]
        val second = bitmaps[1]
        val width = first.width
        val height = first.height

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (first.getPixel(x, y) != second.getPixel(x, y)) {
                    return y
                }
            }
        }
        return 0
    }

    // 检测固定下部分高度
    private fun detectFooterHeight(bitmaps: List<Bitmap>): Int {
        if (bitmaps.size < 2) return 0
        val first = bitmaps[0]
        val second = bitmaps[1]
        val width = first.width
        val height = first.height

        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                if (first.getPixel(x, y) != second.getPixel(x, y)) {
                    return height - (y + 1)
                }
            }
        }
        return 0
    }

    // 检测内容区域的重复高度（修正后添加 footerHeight 参数）
    private fun findOverlapHeight(prev: Bitmap, curr: Bitmap, headerHeight: Int, footerHeight: Int, contentHeight: Int): Int {
        val width = prev.width
        val maxOverlap = minOf(contentHeight, curr.height - headerHeight - footerHeight)

        for (overlap in 0 until maxOverlap) {
            var isMatch = true
            for (x in 0 until width) {
                val prevY = headerHeight + contentHeight - overlap - 1
                val currY = headerHeight + overlap
                if (prevY >= prev.height || currY >= curr.height) {
                    isMatch = false
                    break
                }
                if (prev.getPixel(x, prevY) != curr.getPixel(x, currY)) {
                    isMatch = false
                    break
                }
            }
            if (!isMatch) return overlap
        }
        return maxOverlap
    }


    // 自定义绘制方法
    private fun Canvas.drawBitmap(
        bitmap: Bitmap,
        left: Float,
        top: Float,
        paint: android.graphics.Paint?,
        srcX: Int,
        srcY: Int,
        srcWidth: Int,
        srcHeight: Int
    ) {
        drawBitmap(
            bitmap,
            android.graphics.Rect(srcX, srcY, srcX + srcWidth, srcY + srcHeight),
            android.graphics.RectF(left, top, left + srcWidth, top + srcHeight),
            paint
        )
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