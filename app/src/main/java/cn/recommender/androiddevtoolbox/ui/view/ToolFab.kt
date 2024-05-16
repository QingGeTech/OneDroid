package cn.recommender.androiddevtoolbox.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.ColorInt
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.ui.fragment.ToolsFragment
import cn.recommender.androiddevtoolbox.util.ViewUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class ToolFab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @ColorInt backgroundTintColor: Int
) : FloatingActionButton(context, attrs, defStyleAttr) {

    init {
        compatElevation = 0f
        elevation = 0f
        scaleType = ScaleType.FIT_CENTER
        shapeAppearanceModel =
            ShapeAppearanceModel.builder().setAllCornerSizes(ViewUtil.dpToPx(context, 16f)).build()
        backgroundTintList =
            ColorStateList.valueOf(backgroundTintColor)
    }

    fun setListener(onDrag: (newX: Int, newY: Int) -> Unit, onClick: () -> Unit) {
        var offsetX = 0
        var offsetY = 0
        var downX = 0
        var downY = 0

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX.toInt()
                    downY = event.rawY.toInt()
                    offsetX = event.rawX.toInt() - (layoutParams as WindowManager.LayoutParams).x
                    offsetY = event.rawY.toInt() - (layoutParams as WindowManager.LayoutParams).y
                }

                MotionEvent.ACTION_MOVE -> {
                    onDrag(event.rawX.toInt() - offsetX, event.rawY.toInt() - offsetY)

                }

                MotionEvent.ACTION_UP -> {
                    if (abs(event.rawX.toInt() - downX) < 20 && abs(event.rawY.toInt() - downY) < 20) {
                        onClick()
                    }
                }
            }
            true
        }
    }

}