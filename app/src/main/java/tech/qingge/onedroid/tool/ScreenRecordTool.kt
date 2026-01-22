package tech.qingge.onedroid.tool

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.activity.result.ActivityResult
import androidx.core.content.IntentCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.scopes.ServiceScoped
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.R
import tech.qingge.onedroid.databinding.LayoutFloatingWindowBinding
import tech.qingge.onedroid.service.MediaProjectionService
import tech.qingge.onedroid.ui.activity.MediaProjectionPermissionActivity
import tech.qingge.onedroid.ui.activity.ScreenRecordResultActivity
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.util.DeviceUtil
import tech.qingge.onedroid.util.RandomUtil
import java.io.File
import javax.inject.Inject

@ServiceScoped
class ScreenRecordTool @Inject constructor(val appContext: Application) {

    private lateinit var resetBtn: () -> Unit
    var mediaProjectionService: MediaProjectionService? = null
    var serviceConnection: ServiceConnection? = null

    private var virtualDisplay: VirtualDisplay? = null
    private var mediaRecorder: MediaRecorder? = null

    private lateinit var recordSavePath: String

    private lateinit var binding: LayoutFloatingWindowBinding


    private fun prepareMediaRecorder(mediaRecorder: MediaRecorder) {
        recordSavePath = getRandomFilePath()
        mediaRecorder.apply {
//            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(recordSavePath)
            setVideoSize(
                DeviceUtil.getScreenWidth(appContext), DeviceUtil.getScreenHeight(appContext)
            )
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
//            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncodingBitRate(5000 * 1000)
            setVideoFrameRate(30)
            prepare()
        }
    }

    private fun getRandomFilePath(): String {
        val dir = File("${appContext.externalCacheDir.toString()}/ScreenRecord")
        if (!dir.exists()) {
            dir.mkdir()
        }
        recordSavePath = "$dir/${RandomUtil.uuid()}.mp4"
        return recordSavePath
    }

    fun start(binding: LayoutFloatingWindowBinding, resetBtn: () -> Unit) {
        this.binding = binding
        this.resetBtn = resetBtn
        applyPermission()
    }


    fun applyPermission() {
        val intentFilter =
            IntentFilter(Constants.LOCAL_BROADCAST_ACTION_MEDIA_PROJECTION_PERMISSION_RESULT)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null || intent.action != Constants.LOCAL_BROADCAST_ACTION_MEDIA_PROJECTION_PERMISSION_RESULT) {
                    return
                }

                val activityResult = IntentCompat.getParcelableExtra(
                    intent,
                    "activityResult",
                    ActivityResult::class.java
                )!!

                if (activityResult.resultCode == Activity.RESULT_OK) {
                    bindService(appContext, activityResult)
                } else {
                    Dialogs.showMessageTips(
                        appContext, appContext.getString(R.string.permission_not_granted)
                    )
                }
                LocalBroadcastManager.getInstance(appContext)
                    .unregisterReceiver(this)
            }
        }
        LocalBroadcastManager.getInstance(appContext).registerReceiver(receiver, intentFilter)

        val atyIntent = Intent(appContext, MediaProjectionPermissionActivity::class.java)
        atyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(atyIntent)
    }

    private fun bindService(appContext: Context, activityResult: ActivityResult) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mediaProjectionService =
                    (service as MediaProjectionService.Binder).getService()
                mediaRecorder = MediaRecorder()
                prepareMediaRecorder(mediaRecorder!!)
                virtualDisplay =
                    mediaProjectionService!!.createVirtualDisplay(mediaRecorder!!.surface)
                Handler(Looper.getMainLooper()).postDelayed({
                    startRecord()
                }, 300)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                serviceConnection = null
                mediaProjectionService = null
                stopRecord()
            }
        }
        val intent = Intent(appContext, MediaProjectionService::class.java)
        intent.putExtra("activityResult", activityResult)
        appContext.bindService(intent, serviceConnection!!, Service.BIND_AUTO_CREATE)
    }


    private fun startRecord() {

        mediaProjectionService!!.setOnStopListener {
            stopRecord()
        }

        mediaRecorder!!.start()
        binding.btnControl.setImageResource(R.drawable.ic_stop_circle)
        binding.btnControl.setOnClickListener {
            stopRecord()
        }
    }

    private fun stopRecord() {
        resetBtn()

        val intent = Intent(appContext, ScreenRecordResultActivity::class.java)
        intent.putExtra("filePath", recordSavePath)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        appContext.startActivity(intent)

        release()

    }

    private fun release() {
        mediaRecorder?.release()
        mediaRecorder = null

        virtualDisplay?.release()
        virtualDisplay = null

        mediaProjectionService?.stop()
        mediaProjectionService = null

        if (serviceConnection != null) {
            appContext.unbindService(serviceConnection!!)
            serviceConnection = null
        }
        appContext.stopService(Intent(appContext, MediaProjectionService::class.java))

    }

}