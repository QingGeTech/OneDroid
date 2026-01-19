package tech.qingge.onedroid.util

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import tech.qingge.onedroid.R

class DoublePressBackExit(val activity: Activity) : OnBackPressedCallback(true) {
    private var pressed = false
    override fun handleOnBackPressed() {
        if (pressed) {
            if (activity.isFinishing.not()) {
                activity.moveTaskToBack(true)
            }
        } else {
            pressed = true
            Toast.makeText(activity, R.string.double_press_back_to_exit, Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                pressed = false
            }, 1000)
        }
    }
}