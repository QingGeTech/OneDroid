package cn.recommender.androiddevtoolbox.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityScreenRecordResultBinding
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.MediaUtil

class ScreenRecordResultActivity : BaseActivity() {

    private lateinit var binding: ActivityScreenRecordResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScreenRecordResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(
                v.paddingLeft, statusBarInsets.top, v.paddingRight, navigationBarInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

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
                                Toast.makeText(
                                    this@ScreenRecordResultActivity,
                                    getString(R.string.save_success),
                                    Toast.LENGTH_LONG
                                ).show()
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