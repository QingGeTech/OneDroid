package tech.qingge.onedroid.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseForegroundService
import tech.qingge.onedroid.data.local.sp.SpApi
import tech.qingge.onedroid.databinding.LayoutFloatingWindowBinding
import tech.qingge.onedroid.tool.LayoutInspectTool
import tech.qingge.onedroid.tool.PickColorTool
import tech.qingge.onedroid.tool.PickTextTool
import tech.qingge.onedroid.tool.ScreenRecordTool
import tech.qingge.onedroid.util.DeviceUtil
import javax.inject.Inject

@AndroidEntryPoint
class FloatingWindowService : BaseForegroundService() {

    @Inject
    lateinit var windowManager: WindowManager

    @Inject
    lateinit var pickColorTool: PickColorTool

    @Inject
    lateinit var pickTextTool : PickTextTool

    @Inject
    lateinit var screenRecordTool: ScreenRecordTool

    @Inject
    lateinit var layoutInspectTool: LayoutInspectTool


    @Inject
    lateinit var spApi: SpApi

    private lateinit var binding: LayoutFloatingWindowBinding

    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action == Constants.LOCAL_BROADCAST_ACTION_THEME_CHANGE) {
                reCreateFloatingWindow()
            }
        }
    }

    private fun reCreateFloatingWindow() {
        windowManager.removeView(binding.root)
        createFloatingWindow()
    }

    inner class Binder : android.os.Binder() {
        fun getService(): FloatingWindowService = this@FloatingWindowService
    }

    override fun onBind(intent: Intent?): IBinder {
        return Binder()
    }

    override fun getNotificationData(): NotificationData {
        return NotificationData(
            Constants.NOTIFICATION_NOTIFY_ID_FAB,
            getString(R.string.app_name),
            getString(R.string.floating_window_showing)
        )
    }

    override fun onCreate() {
        super.onCreate()
        createFloatingWindow()

        val filter = IntentFilter(Constants.LOCAL_BROADCAST_ACTION_THEME_CHANGE)
        LocalBroadcastManager.getInstance(this).registerReceiver(themeChangeReceiver, filter)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    private fun createFloatingWindow() {
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (DeviceUtil.getScreenWidth(this@FloatingWindowService) * 0.7).toInt()
            y = (DeviceUtil.getScreenHeight(this@FloatingWindowService) * 0.5).toInt()
        }

        val themeContext = DynamicColors.wrapContextIfAvailable(
            applicationContext,
            DynamicColorsOptions.Builder().setContentBasedSource(spApi.getThemeColor()).build()
        )

        binding =
            LayoutFloatingWindowBinding.inflate(LayoutInflater.from(themeContext), null, false)
        binding.root.apply {
            onDrag = { x, y ->
                layoutParams.x = x
                layoutParams.y = y
                windowManager.updateViewLayout(binding.root, layoutParams)
            }
            onTouchOutside = {
//                hideMenu()
            }
        }
        binding.btnControl.setOnClickListener {
            if (binding.llMenu.isVisible) {
                hideMenu()
            } else {
                binding.llMenu.visibility = View.VISIBLE
                binding.btnControl.setImageResource(R.drawable.ic_subtract)
            }
        }

        listOf(
            binding.btnPickColor,
            binding.btnTextOcr,
            binding.btnScrollScreenshot,
            binding.btnLayoutInspect,
            binding.btnScreenRecord
        ).forEach { it.setOnClickListener(this::onClick) }

        windowManager.addView(binding.root, layoutParams)
    }

    private fun onClick(v: View) {
        hideMenu()
        when (v.id) {
            binding.btnPickColor.id -> pickColorTool.start(binding.root)
            binding.btnTextOcr.id -> pickTextTool.start(binding.root)
            binding.btnLayoutInspect.id -> layoutInspectTool.start(binding.root)
            binding.btnScreenRecord.id -> screenRecordTool.start(binding){
                hideMenu()
                binding.btnControl.setOnClickListener {
                    if (binding.llMenu.isVisible) {
                        hideMenu()
                    } else {
                        binding.llMenu.visibility = View.VISIBLE
                        binding.btnControl.setImageResource(R.drawable.ic_subtract)
                    }
                }
            }
        }
    }


    private fun hideMenu() {
        binding.llMenu.visibility = View.GONE
        binding.btnControl.setImageResource(R.drawable.ic_add)
    }

    override fun onDestroy() {
        super.onDestroy()
        // if service killed by system, remove fab
        try {
            windowManager.removeView(binding.root)
        } catch (_: Exception) {
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(themeChangeReceiver)
    }

}