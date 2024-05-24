package cn.recommender.androiddevtoolbox.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

/**
 * TODO: 如果直接覆盖在屏幕上层，事件传递不到下层，因为是不同的window
 * 其他方法：
 * 1. 提升为系统应用，使用MONITOR_INPUT权限
 * 2. 获取root权限，使用getevent命令
 */
class TouchTraceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private var touchTraceView: TouchTraceView? = null
        fun startTouchTrace(context: Context) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (touchTraceView != null) {
                return
            }

            touchTraceView = TouchTraceView(context)
            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )

            windowManager.addView(touchTraceView, layoutParams)
        }

        fun stopTouchTrace(context: Context) {
            if (touchTraceView == null) {
                return
            }
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(touchTraceView)
            touchTraceView = null
        }
    }


    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
    }

    private var point: Point? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (point == null) {
                    point = Point()
                }
                point!!.x = event.x.toInt()
                point!!.y = event.y.toInt()
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                point = null
                invalidate()
            }
        }
        return false
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        super.dispatchTouchEvent(event)
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.TRANSPARENT)
        if (point == null) {
            return
        }

        canvas.drawCircle(point!!.x.toFloat(), point!!.y.toFloat(), 20f, paint)
    }


}