package cn.recommender.androiddevtoolbox.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class CircleColorPaletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        val gradientColors = intArrayOf(
            0xFFFF0000.toInt(),
            0xFFFF00FF.toInt(),
            0xFF0000FF.toInt(),
            0xFF00FFFF.toInt(),
            0xFF00FF00.toInt(),
            0xFFFFFF00.toInt(),
            0xFFFF0000.toInt()
        )
        val shader = SweepGradient(0f, 0f, gradientColors, null)
        paint.shader = shader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (min(width, height) / 2).toFloat(),
            paint
        )
    }


}