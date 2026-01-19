package tech.qingge.onedroid.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.entity.CardData
import javax.inject.Inject
import kotlin.math.pow


@AndroidEntryPoint
class DeviceInfoScreenFragment @Inject constructor() : DeviceInfoBaseFragment() {


    override suspend fun initCardDataList(): List<CardData> {
        return mutableListOf(
            CardData(getString(R.string.screen_info), getScreenInfo())
        )
    }

    @SuppressLint("DefaultLocale")
    private fun getScreenInfo(): MutableList<Pair<String, String>> {

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        var refreshRate = 0f

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = requireContext().display
            display.getRealMetrics(displayMetrics)
            refreshRate = display.refreshRate
        } else {
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            refreshRate = windowManager.defaultDisplay.refreshRate
        }

        val widthPixels = displayMetrics.widthPixels    // 屏幕宽度（像素）
        val heightPixels = displayMetrics.heightPixels  // 屏幕高度（像素）
        val densityDpi = displayMetrics.densityDpi      // 屏幕 DPI
        val xdpi = displayMetrics.xdpi                  // 屏幕 x 方向 DPI
        val ydpi = displayMetrics.ydpi                  // 屏幕 y 方向 DPI
        val density = displayMetrics.density            // 屏幕密度（基于 160dpi，1.0 = mdpi）
        val scaledDensity = displayMetrics.scaledDensity // 字体缩放比例（通常与用户设置有关）


        val screenWidthInch = (widthPixels / xdpi).toDouble()
        val screenHeightInch = (heightPixels / ydpi).toDouble()

        val screenInch =
            kotlin.math.sqrt(screenWidthInch.pow(2) + screenHeightInch.pow(2))  // 计算屏幕物理尺寸（英寸）

        return mutableListOf(
            Pair(getString(R.string.screen_width_px), widthPixels.toString()),
            Pair(getString(R.string.screen_height_px), heightPixels.toString()),
            Pair(getString(R.string.screen_density_dpi), densityDpi.toString()),
            Pair(getString(R.string.screen_x_dpi), xdpi.toInt().toString()),
            Pair(getString(R.string.screen_y_dpi), ydpi.toInt().toString()),
            Pair(getString(R.string.screen_density), density.toString()),
            Pair(getString(R.string.screen_scaled_density), scaledDensity.toString()),
            Pair(getString(R.string.screen_refresh_rate), "${refreshRate.toInt()}Hz"),
            Pair(
                getString(R.string.screen_width_inch),
                String.format("%.1f", screenWidthInch)
            ),
            Pair(getString(R.string.screen_height_inch), String.format("%.1f", screenHeightInch)),
            Pair(getString(R.string.screen_inch), String.format("%.1f", screenInch)),

            )
    }

}