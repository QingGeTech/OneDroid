package cn.recommender.androiddevtoolbox.ui.view.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.forEachIndexed

class ColorPickerViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var huePickerView: HuePickerView
    private lateinit var svPickerView: SVPickerView

    public lateinit var onColorChangeListener: OnColorChangeListener

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        forEachIndexed { _, view ->
            if (view is HuePickerView) {
                huePickerView = view
            }
            if (view is SVPickerView) {
                svPickerView = view
            }
        }

        huePickerView.onHueChangeListener = object : HuePickerView.OnHueChangeListener {
            override fun onHueChange(hue: Float) {
                svPickerView.hue = hue
                callbackColorChange()
            }
        }
        svPickerView.onSVChangeListener = object : SVPickerView.OnSVChangeListener {
            override fun onSVChange(saturation: Float, brightness: Float) {
                callbackColorChange()
            }
        }
    }


    fun startColorPick(themeColor: Int) {
        val hsvArr = FloatArray(3)
        Color.colorToHSV(themeColor, hsvArr)
        //TODO: 小球和色彩渐变
        huePickerView.initHue(hsvArr[0])
        svPickerView.initSV(hsvArr[1], hsvArr[2])
        svPickerView.hue = hsvArr[0]
        callbackColorChange()
    }

    private fun callbackColorChange() {
        onColorChangeListener.onColorChange(
            Color.HSVToColor(
                floatArrayOf(
                    svPickerView.hue, svPickerView.getSaturation(), svPickerView.getBrightness()
                )
            )
        )
    }

    fun stopColorPick() {
        onColorChangeListener.onColorChange(-1)
    }


    interface OnColorChangeListener {
        fun onColorChange(color: Int)
    }


}



