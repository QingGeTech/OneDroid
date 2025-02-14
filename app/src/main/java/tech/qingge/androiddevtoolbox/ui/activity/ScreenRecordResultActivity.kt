package tech.qingge.androiddevtoolbox.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.MediaController
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseActivity
import tech.qingge.androiddevtoolbox.databinding.ActivityScreenRecordResultBinding
import tech.qingge.androiddevtoolbox.ui.dialog.Dialogs
import tech.qingge.androiddevtoolbox.util.LogUtil
import tech.qingge.androiddevtoolbox.util.MediaUtil

class ScreenRecordResultActivity : BaseActivity<ActivityScreenRecordResultBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews(intent)

    }

    private fun initViews(intent: Intent) {
        val filePath = intent.getStringExtra("filePath")
        binding.video.apply {
            setVideoPath(filePath)
            setMediaController(MediaController(this@ScreenRecordResultActivity))
            setOnPreparedListener {
                LogUtil.d("onPrepared")
            }
            setOnCompletionListener {
                LogUtil.d("onCompletion")
            }
            setOnErrorListener { _, _, _ ->
                LogUtil.d("onError")
                false
            }
            start()
        }

        binding.toolbar.apply {
            setNavigationOnClickListener { finish() }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.save -> {
                        MediaUtil.saveVideo(this@ScreenRecordResultActivity, filePath!!) {
                            if (it) {
                                Dialogs.showMessageTips(
                                    this@ScreenRecordResultActivity,
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