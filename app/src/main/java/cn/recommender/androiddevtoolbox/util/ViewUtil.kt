package cn.recommender.androiddevtoolbox.util

import android.content.Context
import cn.recommender.androiddevtoolbox.R

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

}