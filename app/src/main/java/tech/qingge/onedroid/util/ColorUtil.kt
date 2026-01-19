package tech.qingge.onedroid.util

import android.graphics.Color

object ColorUtil {
    fun colorHex(color: Int): String {
        return String.format("#%08X", color)
    }

    fun getContrastingColor(color: Int): Int {
        // 提取颜色的红、绿、蓝分量
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        // 计算颜色的亮度
        val brightness = (0.299 * red + 0.587 * green + 0.114 * blue)

        // 根据亮度选择对比色
        return if (brightness > 186) Color.BLACK else Color.WHITE
    }
}