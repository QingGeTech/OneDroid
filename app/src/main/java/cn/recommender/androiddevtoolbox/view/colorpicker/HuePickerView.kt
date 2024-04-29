package cn.recommender.androiddevtoolbox.view.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.recommender.androiddevtoolbox.util.LogUtil

class HuePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    lateinit var point: Point

    private val pointPaint = Paint().apply {
        color = Color.WHITE
    }
    var onHueChangeListener: OnHueChangeListener? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.actionMasked) {
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                    LogUtil.d("onTouchEvent:$event")
                    point.y = event.y.toInt().coerceIn(0, height)
                    postInvalidate()
                    onHueChangeListener?.onHueChange(point.y.toFloat() / height * 360)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val colors = IntArray(360)

        for (i in 0 until 360) {
            val color = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
            colors[i] = color
        }

        val shader =
            LinearGradient(0f, 0f, 0f, height.toFloat(), colors, null, Shader.TileMode.CLAMP)

        paint.shader = shader
        point = Point(width / 2, width / 2)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(
            0f, 0f, width.toFloat(), height.toFloat(), width.toFloat(), width.toFloat(), paint
        )
        canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), (width / 2).toFloat(), pointPaint)
    }

    fun initHue(hue: Float) {
        point.y = (hue/360 * height).toInt()
        postInvalidate()
    }

    interface OnHueChangeListener {
        fun onHueChange(hue: Float)
    }
}