package cn.recommender.androiddevtoolbox.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object SoftKeyboardUtil {
    fun showSoftInput(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

    }
}