package cn.recommender.androiddevtoolbox.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class FloatingFrameLayout(
    context: Context
) : FrameLayout(context) {

    var onDrag: ((newX: Int, newY: Int) -> Unit)? = null
    var onTouchOutside: (() -> Unit)? = null


    private var offsetX = 0
    private var offsetY = 0
    private var downX = 0
    private var downY = 0

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_OUTSIDE ->
                onTouchOutside?.invoke()

            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX.toInt()
                downY = event.rawY.toInt()
                offsetX = event.rawX.toInt() - (layoutParams as WindowManager.LayoutParams).x
                offsetY = event.rawY.toInt() - (layoutParams as WindowManager.LayoutParams).y
            }

            MotionEvent.ACTION_MOVE -> {
                onDrag?.invoke(event.rawX.toInt() - offsetX, event.rawY.toInt() - offsetY)
            }

            MotionEvent.ACTION_UP -> {
                if (abs(event.rawX.toInt() - downX) > 20 || abs(event.rawY.toInt() - downY) > 20) {
                    return false
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}