package tech.qingge.onedroid.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View

class MagnifyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private lateinit var scaledBitmap: Bitmap

    private var scale: Float = 3f
    private val point: Point = Point(0, 0)

    private val pointPaint: Paint = Paint().apply {
        color = Color.GRAY
        isAntiAlias = true
    }
    private val imgPaint: Paint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }

    fun setTargetView(targetView: View) {

        val bitmap = Bitmap.createBitmap(
            targetView.width, targetView.height, Bitmap.Config.ARGB_8888
        )

        val canvas2 = Canvas(bitmap)
        targetView.draw(canvas2)
        scaledBitmap = Bitmap.createScaledBitmap(
            bitmap, bitmap.width * scale.toInt(), bitmap.height * scale.toInt(), true
        )

        updatePoint(targetView.width / 2, targetView.height / 2)
    }

    fun updatePoint(x: Int, y: Int) {
        point.x = x
        point.y = y
        postInvalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (this::scaledBitmap.isInitialized) {

            val path = Path()
            path.addRoundRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                20f,
                20f,
                Path.Direction.CW
            )

            canvas.clipPath(path)

            val newBitmap = Bitmap.createBitmap(
                scaledBitmap,
                (point.x * scale - width / 2).toInt().coerceIn(0, scaledBitmap.width - width),
                (point.y * scale - height / 2).toInt().coerceIn(0, scaledBitmap.height - height),
                width,
                height,
            )

            canvas.drawBitmap(
                newBitmap,
                0f,
                0f,
                imgPaint
            )

        }

        canvas.drawCircle(width / 2f, height / 2f, 10f, pointPaint)

    }

}