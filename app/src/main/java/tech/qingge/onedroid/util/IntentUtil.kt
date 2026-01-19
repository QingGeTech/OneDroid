package tech.qingge.onedroid.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

object IntentUtil {
    fun gotoLauncher(context: Context) {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(homeIntent)
    }

    fun openAccessibilityServiceSetting(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @Suppress("DEPRECATION")
    fun getPrintableBundle(bundle: Bundle?): String {
        if (bundle == null || bundle.keySet().isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        bundle.keySet().forEachIndexed { index, key ->
            sb.append("$key:${bundle[key]}")
            if (index != bundle.keySet().size - 1) {
                sb.append("\n")
            }
        }
        return sb.toString()
    }


    fun parseFlag(flag: Int, flags: Map<Int, String>): String {
        val sb = StringBuilder()
        flags.forEach {
            if (flag and it.key != 0) {
                sb.append(it.value)
                sb.append(" | ")
            }
        }
        return sb.toString().dropLast(3)
    }

}