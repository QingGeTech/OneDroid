package cn.recommender.androiddevtoolbox.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityScrollScreenshotResultBinding
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import cn.recommender.androiddevtoolbox.util.MediaUtil
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.features2d.BFMatcher
import org.opencv.features2d.SIFT
import org.opencv.imgproc.Imgproc
import java.io.File


class ScrollScreenshotResultActivity : BaseActivity<ActivityScrollScreenshotResultBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initLocal()

        initViews(intent)

    }

    private fun initViews(intent: Intent) {
        val filePath = intent.getStringExtra("filePath")
        val bitmap = BitmapFactory.decodeFile(filePath)

        binding.photoView.setImageBitmap(bitmap)
        binding.toolbar.apply {
            setNavigationOnClickListener { finish() }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.save -> {
                        MediaUtil.saveImage(this@ScrollScreenshotResultActivity, filePath!!) {
                            if (it) {
                                Dialogs.showMessageTips(
                                    this@ScrollScreenshotResultActivity,
                                    getString(R.string.save_success)
                                )
                            }
                        }
                    }

                }
                true
            }
        }

    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initViews(intent)
    }

}