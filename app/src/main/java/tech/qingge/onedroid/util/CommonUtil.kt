package tech.qingge.onedroid.util

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.view.Menu
import androidx.core.net.toUri
import androidx.core.os.ConfigurationCompat
import androidx.core.view.get

object CommonUtil {

    /**
     * find position of menuItem in a menu
     */
    fun findMenuPosition(menu: Menu, menuItemId: Int): Int {
        for (i in (0..<menu.size())) {
            if (menuItemId == menu[i].itemId) {
                return i
            }
        }
        return -1;
    }

    fun openUrl(activity: Activity, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        activity.startActivity(intent)
    }

    fun isChinese(configuration: Configuration): Boolean {
        try {
            val locale = ConfigurationCompat.getLocales(configuration)[0]
            return locale!!.language.startsWith("zh")
        } catch (e: Exception) {
            return false
        }
    }

}