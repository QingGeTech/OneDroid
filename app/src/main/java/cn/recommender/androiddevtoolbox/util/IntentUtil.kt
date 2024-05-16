package cn.recommender.androiddevtoolbox.util

import android.content.Context
import android.content.Intent

object IntentUtil {
    fun gotoLauncher(context: Context) {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(homeIntent)
    }
}