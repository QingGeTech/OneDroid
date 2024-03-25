package cn.recommender.androiddevtoolbox.view

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
class CircleColorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var color: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleColorView)
        color = typedArray.getColor(R.styleable.CircleColorView_color, Color.BLACK)
        paint.color = color
        typedArray.recycle()
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