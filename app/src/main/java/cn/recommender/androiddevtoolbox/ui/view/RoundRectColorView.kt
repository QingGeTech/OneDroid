package cn.recommender.androiddevtoolbox.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.recommender.androiddevtoolbox.R
import kotlin.math.min

/**
 * Circle pure color view
 */
class RoundRectColorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    fun setColor(color: Int) {
        paint.color = color
        invalidate()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var corner: Float = 20f

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RoundRectColorView)
        paint.color = typedArray.getColor(R.styleable.RoundRectColorView_color, Color.BLACK)
        corner = typedArray.getDimension(R.styleable.RoundRectColorView_corner, 20f)
        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), corner, corner, paint)
    }


}