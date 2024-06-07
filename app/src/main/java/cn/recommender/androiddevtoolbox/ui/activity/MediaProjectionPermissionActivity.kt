package cn.recommender.androiddevtoolbox.ui.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.recommender.androiddevtoolbox.Constants
import cn.recommender.androiddevtoolbox.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MediaProjectionService使用，申请权限
 */
@AndroidEntryPoint
class MediaProjectionPermissionActivity : AppCompatActivity() {

    @Inject
    lateinit var mediaProjectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val activityResultCallback = ActivityResultCallback<ActivityResult> { activityResult ->
            val broadIntent =
                Intent(Constants.LOCAL_BROADCAST_ACTION_MEDIA_PROJECTION_PERMISSION_RESULT)
            broadIntent.putExtra("activityResult", activityResult)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadIntent)
            finish()
        }

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), activityResultCallback
        )
        val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
        activityResultLauncher.launch(permissionIntent)
    }
}