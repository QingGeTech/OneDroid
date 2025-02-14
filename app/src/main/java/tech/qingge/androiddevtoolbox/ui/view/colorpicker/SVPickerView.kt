package tech.qingge.androiddevtoolbox.ui.view.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff.Mode
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import tech.qingge.androiddevtoolbox.util.LogUtil
import tech.qingge.androiddevtoolbox.util.ViewUtil


class SVPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    public var hue = 0f
        set(value) {
            field = value
            initShader()
            invalidate()
        }

    private var point: Point = Point(100, 100)

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = Color.WHITE
    }

    var onSVChangeListener: OnSVChangeListener? = null


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                    LogUtil.d("onTouchEvent:$event, x:${x}, width:${width}")
                    point.x = event.x.toInt().coerceIn(0, width)
                    point.y = event.y.toInt().coerceIn(0, height)

                    val saturation = getSaturation()
                    val brightness = getBrightness()
                    onSVChangeListener?.onSVChange(saturation, brightness)
                    postInvalidate()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun getSaturation(): Float {
        return point.x.toFloat() / width
    }

    fun getBrightness(): Float {
        return (1 - point.y.toFloat() / height)
    }

    private fun initShader() {

        // 创建饱和度渐变
        // 从左到右: 白色 -> 色相颜色
        val saturationShader: Shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            intArrayOf(
                Color.HSVToColor(floatArrayOf(hue, 0f, 1f)),
                Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
            ),
            null, TileMode.CLAMP
        )

        // 创建亮度渐变
        // 从上到下：白色 -> 黑色
        val brightnessShader: Shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.HSVToColor(floatArrayOf(hue, 0f, 1f)),
                Color.HSVToColor(floatArrayOf(hue, 0f, 0f))
            ),
            null, TileMode.CLAMP
        )

        // 叠加两种渐变
        val shader = ComposeShader(brightnessShader, saturationShader, Mode.MULTIPLY)

        paint.shader = shader
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initShader()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            ViewUtil.dpToPx(context, 20f),
            ViewUtil.dpToPx(context, 20f),
            paint
        )
        canvas.drawCircle(
            point.x.toFloat(),
            point.y.toFloat(),
            ViewUtil.dpToPx(context, 10f),
            pointPaint
        )
    }

    fun initSV(saturation: Float, brightness: Float) {
        point.x = (saturation * width).toInt()
        point.y = ((1 - brightness) * height).toInt()
        postInvalidate()
    }


    interface OnSVChangeListener {
        fun onSVChange(saturation: Float, brightness: Float)
    }

}