package cn.recommender.androiddevtoolbox.util

import android.view.Menu
import android.view.MenuItem
import androidx.core.view.get

object CommonUtils {

    /**
     * find position of menuItem in a menu
     */
    fun findMenuPosition(menu: Menu, menuItem: MenuItem): Int {
        for (i in (0..<menu.size())) {
            if (menuItem.itemId == menu[i].itemId) {
                return i
            }
        }
        return -1;
    }

}