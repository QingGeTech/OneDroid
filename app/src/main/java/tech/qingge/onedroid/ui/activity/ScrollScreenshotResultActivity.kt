package tech.qingge.onedroid.ui.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import org.opencv.android.OpenCVLoader
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseActivity
import tech.qingge.onedroid.databinding.ActivityScrollScreenshotResultBinding
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.util.MediaUtil


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