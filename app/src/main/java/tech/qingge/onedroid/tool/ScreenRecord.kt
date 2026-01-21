package tech.qingge.onedroid.tool

import android.app.Application
import android.content.Intent
import android.content.res.ColorStateList
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import dagger.hilt.android.scopes.ServiceScoped
import tech.qingge.onedroid.R
import tech.qingge.onedroid.databinding.FloatingScreenRecordBinding
import tech.qingge.onedroid.ui.activity.ScreenRecordResultActivity
import tech.qingge.onedroid.ui.view.DraggableFrameLayout
import tech.qingge.onedroid.util.DeviceUtil
import tech.qingge.onedroid.util.RandomUtil
import tech.qingge.onedroid.util.ViewUtil
import java.io.File
import javax.inject.Inject

@ServiceScoped
class ScreenRecord @Inject constructor(val appContext: Application) : BaseMediaProjectionTool() {

    private lateinit var binding: FloatingScreenRecordBinding

    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var mediaRecorder: MediaRecorder

    private lateinit var recordSavePath: String

    private var isRecording = false
    override fun onServiceConnected() {
        mediaRecorder = MediaRecorder()
        prepareMediaRecorder(mediaRecorder)
        virtualDisplay = mediaProjectionService.createVirtualDisplay(mediaRecorder.surface)
        Handler(Looper.getMainLooper()).postDelayed({
            startRecord()
        }, 300)
    }

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

    override fun init(ffl: DraggableFrameLayout) {
        binding = FloatingScreenRecordBinding.inflate(LayoutInflater.from(ffl.context))
        ffl.addView(binding.root)
        val color = ViewUtil.getColorByStyledAttr(
            ffl.context, R.attr.colorPrimaryContainer
        )

        binding.root.elevation = 0f
        binding.root.backgroundTintList = ColorStateList.valueOf(color)
        binding.root.stateListAnimator = null

        binding.root.setOnClickListener {
            if (isRecording) {
                stopRecord()
            } else {
//                if (this::virtualDisplay.isInitialized) {
//                    startRecord()
//                } else {
                initMediaProjectionService(appContext)
//                }
            }
        }
    }

    private fun startRecord() {
        mediaRecorder.start()
        isRecording = true
        binding.root.text = appContext.getString(R.string.end)
    }

    private fun stopRecord() {
        mediaRecorder.stop()
//        mediaRecorder.release()
        isRecording = false
        binding.root.text = appContext.getString(R.string.start)

        val intent = Intent(appContext, ScreenRecordResultActivity::class.java)
        intent.putExtra("filePath", recordSavePath)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        appContext.startActivity(intent)

        deInit()

//        mediaRecorder.reset()
//        prepareMediaRecorder(mediaRecorder)
    }

    override fun deInit() {
        super.stopMediaProjectionService(appContext)
        if (this::mediaRecorder.isInitialized) {
            mediaRecorder.release()
        }
        if (this::virtualDisplay.isInitialized) {
            virtualDisplay.release()
        }
    }
}