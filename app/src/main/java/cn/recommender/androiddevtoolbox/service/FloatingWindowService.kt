package cn.recommender.androiddevtoolbox.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.ServiceCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.recommender.androiddevtoolbox.Constants
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseForegroundService
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.tool.BaseTool
import cn.recommender.androiddevtoolbox.tool.ScreenPickColor
import cn.recommender.androiddevtoolbox.tool.ScreenPickText
import cn.recommender.androiddevtoolbox.tool.ScreenRecord
import cn.recommender.androiddevtoolbox.tool.ScrollScreenshot
import cn.recommender.androiddevtoolbox.ui.view.FloatingFrameLayout
import cn.recommender.androiddevtoolbox.util.DeviceUtil
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FloatingWindowService : BaseForegroundService() {

    @Inject
    lateinit var windowManager: WindowManager

    @Inject
    lateinit var scrollScreenshot: ScrollScreenshot

    @Inject
    lateinit var screenRecord: ScreenRecord

    @Inject
    lateinit var screenPickColor: ScreenPickColor

    @Inject
    lateinit var screenPickText: ScreenPickText

    @Inject
    lateinit var spApi: SpApi

    private lateinit var ffl: FloatingFrameLayout
    var position = -1

    private lateinit var tools: HashMap<Int, BaseTool>

    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action == Constants.LOCAL_BROADCAST_ACTION_THEME_CHANGE) {
                reCreateFloatingWindow()
            }
        }
    }

    private fun reCreateFloatingWindow() {
        windowManager.removeView(ffl)
        createFloatingWindow()
        showFloatingWindow(position)
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
        tools = hashMapOf(
            0 to scrollScreenshot,
            1 to screenRecord,
            2 to screenPickColor,
            3 to screenPickText
        )
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
        ffl = FloatingFrameLayout(
            DynamicColors.wrapContextIfAvailable(
                applicationContext,
                DynamicColorsOptions.Builder().setContentBasedSource(spApi.getThemeColor()).build()
            )
        ).apply {
            onDrag = { x, y ->
                layoutParams.x = x
                layoutParams.y = y
                windowManager.updateViewLayout(ffl, layoutParams)
            }
            visibility = View.GONE
        }
        windowManager.addView(ffl, layoutParams)
    }


    override fun onDestroy() {
        super.onDestroy()
        // if service killed by system, remove fab
        try {
            windowManager.removeView(ffl)
        } catch (_: Exception) {
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(themeChangeReceiver)
    }

    fun showFloatingWindow(position: Int) {
        this.position = position
        tools[position]!!.init(ffl)
        ffl.visibility = View.VISIBLE
    }

    fun closeFloatingWindow(position: Int) {
        this.position = -1
        windowManager.removeView(ffl)
        tools[position]!!.deInit()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


}