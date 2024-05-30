package cn.recommender.androiddevtoolbox.tool

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
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.databinding.FloatingScreenPickColorBinding
import cn.recommender.androiddevtoolbox.ui.activity.PickColorActivity
import cn.recommender.androiddevtoolbox.ui.view.FloatingFrameLayout
import cn.recommender.androiddevtoolbox.util.BitmapUtil
import cn.recommender.androiddevtoolbox.util.DeviceUtil
import cn.recommender.androiddevtoolbox.util.MediaUtil
import cn.recommender.androiddevtoolbox.util.RandomUtil
import cn.recommender.androiddevtoolbox.util.ViewUtil
import dagger.hilt.android.scopes.ServiceScoped
import java.io.File
import javax.inject.Inject

@ServiceScoped
class ScreenPickColor @Inject constructor(val appContext: Application) : BaseMediaProjectionTool() {

    lateinit var binding: FloatingScreenPickColorBinding

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

    override fun init(ffl: FloatingFrameLayout) {
        binding = FloatingScreenPickColorBinding.inflate(LayoutInflater.from(ffl.context))
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

            binding.root.visibility = View.VISIBLE

            val filePath = saveBitmapToFile(bitmap)

            val intent = Intent(appContext, PickColorActivity::class.java)
            intent.putExtra("filePath", filePath)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            appContext.startActivity(intent)
        }, 200)
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