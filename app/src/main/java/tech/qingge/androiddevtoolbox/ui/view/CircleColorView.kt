package tech.qingge.androiddevtoolbox.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import tech.qingge.androiddevtoolbox.R
import kotlin.math.min

/**
 * Circle pure color view
 */
class CircleColorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    fun setColor(color: Int){
        paint.color = color
        invalidate()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleColorView)
        paint.color = typedArray.getColor(R.styleable.CircleColorView_color, Color.BLACK)
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