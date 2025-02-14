package tech.qingge.androiddevtoolbox.util

import android.content.Context
import android.graphics.drawable.Drawable

object ViewUtil {
    fun dpToPx(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun pxToDp(context: Context, px: Float): Float {
        val scale = context.resources.displayMetrics.density
        return px / scale + 0.5f
    }

    fun getColorByStyledAttr(context:Context, attrId: Int) : Int{
        val attr = context.obtainStyledAttributes(intArrayOf(attrId))
        val color = attr.getColor(0, 0)
        attr.recycle()
        return color
    }

    fun getDrawableByStyledAttr(context: Context, attrId: Int): Drawable? {
        val attr = context.obtainStyledAttributes(intArrayOf(attrId))
        val drawable = attr.getDrawable(0)
        attr.recycle()
        return drawable
    }

}